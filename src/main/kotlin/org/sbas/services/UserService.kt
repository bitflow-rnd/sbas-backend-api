package org.sbas.services

import io.quarkus.cache.*
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.constants.SbasConst
import org.sbas.constants.enums.UserStatCd
import org.sbas.dtos.PagingListDto
import org.sbas.dtos.info.*
import org.sbas.entities.info.InfoCntc
import org.sbas.entities.info.InfoCntcId
import org.sbas.parameters.*
import org.sbas.repositories.InfoAlarmRepository
import org.sbas.repositories.InfoCntcRepository
import org.sbas.repositories.InfoUserRepository
import org.sbas.repositories.UserActivityHistoryRepository
import org.sbas.responses.CommonListResponse
import org.sbas.responses.CommonResponse
import org.sbas.restclients.NaverSensRestClient
import org.sbas.restdtos.NaverSmsMsgApiParams
import org.sbas.restdtos.NaverSmsReqMsgs
import org.sbas.utils.CustomizedException
import org.sbas.utils.TimeUtil
import org.sbas.utils.TokenUtils
import java.security.MessageDigest

@ApplicationScoped
class UserService {

  @Inject
  private lateinit var log: Logger

  @Inject
  private lateinit var userRepository: InfoUserRepository

  @Inject
  private lateinit var cntcRepository: InfoCntcRepository

  @Inject
  private lateinit var activityHistoryRepository: UserActivityHistoryRepository

  @Inject
  private lateinit var infoAlarmRepository: InfoAlarmRepository

  @RestClient
  private lateinit var naverSensClient: NaverSensRestClient

  @Inject
  private lateinit var jwt: JsonWebToken

  @ConfigProperty(name = "restclient.naversens.serviceid")
  private lateinit var naversensserviceid: String

  @CacheName("smsCache")
  private lateinit var smsCache: Cache

  /**
   * 사용자 등록 요청
   */
  @Transactional
  fun reqUserReg(infoUserSaveReq: InfoUserSaveReq): CommonResponse<String> {
    val infoUser = infoUserSaveReq.toEntity(UserStatCd.URST0001)
    infoUser.setRgstAndUpdtUserIdTo(infoUserSaveReq.id)
    userRepository.persist(infoUser)
    return CommonResponse("${infoUserSaveReq.userNm}님 사용자 등록을 요청하였습니다.")
  }

  /**
   * 관리자 사용자 등록
   */
  @Transactional
  fun reg(infoUserSaveReq: InfoUserSaveReq): CommonResponse<String> {
    val infoUser = infoUserSaveReq.toEntity(UserStatCd.URST0002)
    infoUser.setRgstAndUpdtUserIdTo(infoUserSaveReq.id)
    userRepository.persist(infoUser)
    return CommonResponse("등록 성공")
  }

  @Transactional
  fun aprv(request: UserRequest): CommonResponse<String> {
    val findUser = userRepository.findByUserId(request.id)
      ?: throw CustomizedException("선택한 유저 ID가 없습니다.", Response.Status.NOT_FOUND)

    findUser.updateUserStatCdByAdmin(jwt.name ?: "administrator", request.isApproved)
    return CommonResponse("${findUser.id}, ${findUser.userStatCd!!.cdNm}")
  }

  /**
   * 관리자 사용자 정보 관리 화면에서 사용자 목록 조회
   */
  @Transactional
  fun getUsers(param: InfoUserSearchParam): CommonListResponse<UserDetailResponse> {
    val list = userRepository.findInfoUserList(param)
    list.forEach { it.userStatCdNm = it.userStatCd!!.cdNm }
    val count = userRepository.countInfoUserList(param)

    return CommonListResponse(list, count.toInt())
  }

  /**
   * 본인인증 SMS/MMS 메시지 발송
   */
  fun sendIdentifySms(smsSendRequest: SmsSendRequest): CommonResponse<String> {
    val completableFuture =
      smsCache.`as`(CaffeineCache::class.java).getIfPresent<String>(smsSendRequest.to)

    if (completableFuture != null) {
      smsCache.`as`(CaffeineCache::class.java).invalidate(smsSendRequest.to).await().indefinitely()
    }

    val rand = getSmsNumber(smsSendRequest.to)

    val smsTo = NaverSmsReqMsgs("", "", smsSendRequest.to)

    naverSensClient.messages(
      naversensserviceid, NaverSmsMsgApiParams(
        SbasConst.MsgType.SMS, null, SbasConst.MSG_SEND_NO, null, "COMM",
        "안녕하세요. SBAS 인증번호는 [ $rand ]입니다. 감사합니다.", null, null, null, mutableListOf(smsTo), null
      )
    )

    return CommonResponse(rand)
  }

  @CacheResult(cacheName = "smsCache")
  fun getSmsNumber(@CacheKey phoneNumber: String): String {
    var rand = ""
    for (i: Int in 0..5) {
      rand += ('0'..'9').random()
    }

    return rand
  }

  /**
   * 인증번호 확인
   */
  @Transactional
  fun checkCertNo(checkCertNoRequest: CheckCertNoRequest): CommonResponse<String> {
    val completableFuture =
      smsCache.`as`(CaffeineCache::class.java).getIfPresent<String>(checkCertNoRequest.phoneNo)
        ?: throw CustomizedException("인증 시간 초과", Response.Status.BAD_REQUEST)

    val number = completableFuture.get()

    return when {
      number == checkCertNoRequest.certNo -> {
        CommonResponse("인증 성공")
      }

      else -> {
        throw CustomizedException("유효하지 않은 인증번호입니다.", Response.Status.NOT_ACCEPTABLE)
      }
    }
  }

  @Transactional
  fun deleteUser(request: UserIdRequest): CommonResponse<String> {
    request.adminId = jwt.name

    val findUser = userRepository.findByUserId(request.id)
      ?: throw CustomizedException("선택한 유저 ID가 없습니다.", Response.Status.NOT_FOUND)

    findUser.userStatCd = UserStatCd.URST0006
    findUser.updtUserId = request.adminId!!

    return CommonResponse("${request.id} 계정을 삭제하였습니다.")
  }

  @Transactional
  fun modifyPw(modifyPwRequest: ModifyPwRequest): CommonResponse<String> {
    if (jwt.name != modifyPwRequest.id) return CommonResponse("token id와 id가 일치하지 않습니다.")

    val findUser = userRepository.findByUserId(modifyPwRequest.id)
      ?: throw CustomizedException("유저 정보가 없습니다.", Response.Status.NOT_FOUND)

    findUser.changePasswordTo(modifyPwRequest.modifyPw)

    return CommonResponse("${findUser.id}님의 비밀번호가 수정되었습니다.")

  }

  @Transactional
  fun modifyTelno(modifyTelnoRequest: ModifyTelnoRequest): CommonResponse<String> {
    if (jwt.name != modifyTelnoRequest.id) return CommonResponse("token id와 id가 일치하지 않습니다.")

    val findUser = userRepository.findByUserId(modifyTelnoRequest.id)
      ?: throw CustomizedException("유저 정보가 없습니다.", Response.Status.NOT_FOUND)

    findUser.changeTelnoTo(modifyTelnoRequest.modifyTelno)

    return CommonResponse("${findUser.userNm}님의 핸드폰번호가 수정되었습니다.")
  }

  /**
   * 사용자 아이디 중복 체크
   * @param userId
   */
  @Transactional
  fun checkUserId(userId: String?): Pair<Boolean, String> {
    return when {
      userRepository.existByUserId(userId) -> Pair(true, "이미 사용중인 아이디입니다.")
      else -> Pair(false, "사용 가능한 아이디입니다.")
    }
  }

  /**
   * 사용자 전화번호 중복 체크
   * @param telno
   */
  @Transactional
  fun checkTelNo(telno: String): Pair<Boolean, String> {
    return when {
      userRepository.existByTelNo(telno) -> Pair(true, "이미 사용중인 번호입니다.")
      else -> Pair(false, "사용 가능한 번호입니다.")
    }
  }

  /**
   * 로그인
   */
  @Transactional
  fun login(loginRequest: LoginRequest): CommonResponse<String> {
    val findUser = userRepository.findByUserId(loginRequest.id)
      ?: throw CustomizedException("등록된 ID가 없습니다.", Response.Status.NOT_FOUND)

    return when {
      findUser.pwErrCnt!! >= 5 -> {
        throw CustomizedException("비밀번호 불일치 5회 발생", Response.Status.FORBIDDEN)
      }

      findUser.pw == loginRequest.pw && findUser.userStatCd == UserStatCd.URST0001 -> {
        CommonResponse(SbasConst.ResCode.FAIL_VALIDATION, "사용자 요청이 승인되지 않았습니다.", null)
      }

      findUser.pw == loginRequest.pw && findUser.userStatCd != UserStatCd.URST0001 -> {
        findUser.pwErrCnt = 0
        CommonResponse(TokenUtils.generateUserToken(findUser.id, findUser.userNm))
      }

      else -> {
        findUser.plusPasswordErrorCount()
        throw CustomizedException("사용자 정보가 일치하지 않습니다.", Response.Status.BAD_REQUEST)
      }
    }
  }

  /**
   * 아이디 찾기
   */
  @Transactional
  fun findId(findIdRequest: FindIdRequest): CommonResponse<String?> {
    val findUser = userRepository.findId(findIdRequest)
      ?: throw CustomizedException("정보가 일치하는 ID가 없습니다.", Response.Status.NOT_FOUND)

    return CommonResponse(findUser.id)
  }

  /**
   * 비밀번호 변경
   */
  @Transactional
  fun findPw(modifyPwRequest: ModifyPwRequest): CommonResponse<String?> {
    val findUser = userRepository.findByUserId(modifyPwRequest.id)
      ?: throw CustomizedException("등록된 ID가 없습니다.", Response.Status.NOT_FOUND)

    findUser.pw = modifyPwRequest.modifyPw

    userRepository.persist(findUser)

    return CommonResponse("SUCCESS")
  }

  /**
   * 비밀번호 초기화
   */
  @Transactional
  fun initPw(initPwRequest: InitPwRequest): CommonResponse<String?> {
    val findUser = userRepository.initPw(initPwRequest)
      ?: throw CustomizedException("등록된 ID가 없습니다.", Response.Status.NOT_FOUND)

    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"
    val changePw = (1..20)
      .map { allowedChars.random() }
      .joinToString("")

    val bytes = changePw.toByteArray()
    val md = MessageDigest.getInstance("SHA-512")
    val digest = md.digest(bytes)
    val savePw = digest.fold("") { str, it -> str + "%02x".format(it) }

    findUser.pw = savePw

    userRepository.persist(findUser)

    return CommonResponse(changePw)
  }

  /**
   * 사용자 정보 수정
   */
  @Transactional
  fun modifyInfo(request: InfoUserUpdateReq): CommonResponse<String> {
    if (jwt.name != request.id) return CommonResponse("token id와 id가 일치하지 않습니다.")

    val findUser = userRepository.findByUserId(request.id)
      ?: throw CustomizedException("등록된 ID가 없습니다.", Response.Status.NOT_FOUND)

    findUser.updateTo(request)

    return CommonResponse("SUCCESS")
  }

  /**
   * 사용자 목록 전체 조회(paging, total count 반영, filter X)
   */
  @Transactional
  fun getAllUsers(pageRequest: PageRequest): CommonResponse<PagingListDto> {
    // TODO 응답 수정
    val findUsers = userRepository.findAllInfoUser()
    findUsers.forEach { it.userStatCdNm = it.userStatCd!!.cdNm }
    val totalCnt = userRepository.count()
    val response = PagingListDto(totalCnt, findUsers)
    return CommonResponse(response)
  }

  /**
   * 나와 관련된 사용자 조회
   */
  @Transactional
  fun getMyUsers(): CommonResponse<List<InfoCntc>> {
    val result = cntcRepository.getMyUsers(jwt.name)
    return CommonResponse(result)
  }

  /**
   * 사용자 상세
   */
  @Transactional
  fun getMyUserDetail(mbrId: String): CommonResponse<UserDetailResponse> {
    val userDetail = userRepository.findInfoUserById(mbrId)

    return CommonResponse(userDetail)
  }

  /**
   * 사용자 목록 조회
   */
  @Transactional
  fun getUsersFromUser(param: InfoUserSearchFromUserParam): CommonListResponse<InfoUserListDto> {
    val list = userRepository.findInfoUsersFromUser(param)
    list.forEach { it.userStatCdNm = it.userStatCd!!.cdNm }
    val count = userRepository.countInfoUsersFromUser(param)

    return CommonListResponse(list, count.toInt())
  }

  /**
   * 즐겨찾기 등록
   */
  @Transactional
  fun regFavorite(request: InfoCntcDto): CommonResponse<InfoCntc> {
    val histSeq = cntcRepository.getHistSeq(request.id) ?: 0

    if (cntcRepository.findInfoCntcByUserIdAndMbrId(request.id, request.mbrId) != null) {
      return CommonResponse(SbasConst.ResCode.FAIL, "이미 즐겨찾기에 등록되어 있습니다.", null)
    }

    //유저 존재 확인
    userRepository.findByUserId(request.mbrId) ?: throw CustomizedException(
      "해당 유저를 찾을 수 없습니다.",
      Response.Status.NOT_FOUND
    )

    val insertCntc = InfoCntc(
      id = InfoCntcId(
        userId = request.id,
        histCd = "1",
        histSeq = histSeq + 1,
        mbrId = request.mbrId
      )
    )

    cntcRepository.persist(insertCntc)

    return CommonResponse("즐겨찾기에 등록되었습니다.", insertCntc)
  }

  /**
   * 즐겨찾기 사용자 목록 조회
   */
  @Transactional
  fun getContactUsers(): CommonListResponse<InfoUserListDto> {
    val list = userRepository.findContactedInfoUserListByUserId(jwt.name)
    val count = list.size

    return CommonListResponse(list, count)
  }

  /**
   * 즐겨찾기 삭제
   */
  @Transactional
  fun delFavorite(request: InfoCntcDto): CommonResponse<InfoCntc> {
    val findCntc = cntcRepository.findInfoCntcByUserIdAndMbrId(request.id, request.mbrId)
      ?: return CommonResponse(SbasConst.ResCode.FAIL, "즐겨찾기에 등록되어 있지 않습니다.", null)

    cntcRepository.delete(findCntc)

    return CommonResponse("즐겨찾기에서 삭제되었습니다.", findCntc)
  }

  fun getActivityHistory(userId: String): CommonListResponse<UserActivityHistoryResponse> {
    val histories = activityHistoryRepository.findAllByUserId(userId)
    return CommonListResponse(histories, histories.size)
  }

  /**
   * 알림 목록 조회
   */
  fun getAlarmList(): CommonListResponse<InfoAlarmDto> {
    val receiverId = jwt.name
    val findAlarmList = infoAlarmRepository.findAllByReceiverId(receiverId)

    val alarmDtoList = findAlarmList.map {
      val findSender = userRepository.findByUserId(it.senderId)
      val findReceiver = userRepository.findByUserId(it.receiverId)
      val formattedDate = TimeUtil.formatInstant(it.rgstDttm)

      InfoAlarmDto(
        alarmId = it.alarmId,
        title = it.title,
        detail = it.detail,
        senderId = it.senderId,
        senderName = findSender?.userNm ?: "",
        receiverId = it.receiverId,
        receiverName = findReceiver?.userNm ?: "",
        isRead = it.isRead,
        rgstDttm = formattedDate,
      )
    }

    return CommonListResponse(alarmDtoList)
  }

  fun getAlarmCount(): CommonResponse<Long> {
    val receiverId = jwt.name ?: throw CustomizedException("jwt check", Response.Status.BAD_REQUEST)
    val count = infoAlarmRepository.findUnreadAlarmsByReceiverId(receiverId)
    return CommonResponse(count)
  }

  fun readAlarms(): CommonResponse<String> {
    val receiverId = jwt.name
    val updateCnt = infoAlarmRepository.readAlarmsByReceiverId(receiverId)

    return CommonResponse("${updateCnt}개 알림 읽기 처리 완료")
  }

}