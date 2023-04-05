package org.sbas.services

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.constants.SbasConst
import org.sbas.constants.StatClas
import org.sbas.entities.info.InfoCert
import org.sbas.entities.info.InfoUser
import org.sbas.utils.CustomizedException
import org.sbas.parameters.*
import org.sbas.repositories.InfoCertRepository
import org.sbas.repositories.InfoUserRepository
import org.sbas.responses.CommonResponse
import org.sbas.restclients.NaverSensRestClient
import org.sbas.restparameters.NaverSmsMsgApiParams
import org.sbas.restparameters.NaverSmsReqMsgs
import org.sbas.utils.TokenUtils
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.NotFoundException
import javax.ws.rs.core.Response

@ApplicationScoped
class UserService {

    @Inject
    private lateinit var log: Logger

    @Inject
    private lateinit var userRepository : InfoUserRepository

    @Inject
    private lateinit var certRepository : InfoCertRepository

    @RestClient
    private lateinit var naverSensClient : NaverSensRestClient

    @Inject
    private lateinit var jwt : JsonWebToken

    @ConfigProperty(name = "restclient.naversens.serviceid")
    private lateinit var naversensserviceid: String

    @Transactional
    fun reqUserReg(infoUser: InfoUser): CommonResponse<String> {

        infoUser.rgstUserId = "admin"
        infoUser.updtUserId = "admin"
        infoUser.statClas = StatClas.URST0001

        userRepository.persist(infoUser)

        return CommonResponse("${infoUser.userNm}님 사용자 등록을 요청하였습니다.")
    }

    /**
     * 백오피스에서 어드민(전산담당)이 처리하는 API
     */
    @Transactional
    fun getUsers(searchData: String): CommonResponse<List<InfoUser>> {

        return CommonResponse(userRepository.findLike(searchData))
    }

    /**
     * 본인인증 SMS/MMS 메시지 발송
     */
    @Transactional
    fun sendIdentifySms(smsSendRequest: SmsSendRequest): CommonResponse<String> {
        var rand = ""
        for(i: Int in 0..5){
            rand += ('0'..'9').random()
        }

        val smsTo = NaverSmsReqMsgs("", "", smsSendRequest.to!!)

        naverSensClient.messages(naversensserviceid, NaverSmsMsgApiParams(
            SbasConst.MsgType.SMS, null, SbasConst.MSG_SEND_NO, null, "COMM",
            "안녕하세요. SBAS 인증번호는 [ $rand ]입니다. 감사합니다.", null, null, null, mutableListOf(smsTo), null)
        )

        val now = Instant.now()

        var findCert = certRepository.find("phone_no", smsSendRequest.to!!).firstResult()

        if(findCert == null) {
            val insertCertNo = InfoCert(smsSendRequest.to!!, rand, now, now.plusSeconds(180))

            certRepository.persist(insertCertNo)
        }else {
            findCert.certNo = rand
            findCert.createdDttm = now
            findCert.expiresDttm = now.plusSeconds(180)
        }

        return CommonResponse(rand)
    }

    @Transactional
    fun reg(request: UserRequest): CommonResponse<String> {
        request.adminId = jwt.name

        val findUser = userRepository.findByUserId(request.id)
            ?: throw CustomizedException("선택한 유저 ID가 없습니다.", Response.Status.NOT_FOUND)

        findUser.aprvDttm = Instant.now()
        findUser.updtUserId = request.adminId
        findUser.aprvUserId = request.adminId
        if(request.isApproved) {
            findUser.statClas = StatClas.URST0002
            return CommonResponse("${findUser.userNm}님 사용자 등록을 승인하였습니다.")
        }else {
            findUser.statClas = StatClas.URST0003
            return CommonResponse("${findUser.userNm}님 사용자 등록이 반려되었습니다.")
        }
    }

    @Transactional
    fun deleteUser(request: UserIdRequest): CommonResponse<String> {
        request.adminId = jwt.name

        val findUser = userRepository.findByUserId(request.id)
            ?: throw CustomizedException("선택한 유저 ID가 없습니다.", Response.Status.NOT_FOUND)

        findUser.statClas = StatClas.URST0006
        findUser.updtUserId = request.adminId

        return CommonResponse("${request.id} 계정을 삭제하였습니다.")
    }

    @Transactional
    fun modifyPw(modifyPwRequest: ModifyPwRequest): CommonResponse<String> {
        if(jwt.name != modifyPwRequest.id) return CommonResponse("token id와 id가 일치하지 않습니다.")

        val findUser = userRepository.findByUserId(modifyPwRequest.id)
            ?: throw CustomizedException("유저 정보가 없습니다.", Response.Status.NOT_FOUND)

        findUser.pw = modifyPwRequest.modifyPw

        return CommonResponse("${findUser.userNm}님의 비밀번호가 수정되었습니다.")

    }

    @Transactional
    fun modifyTelno(modifyTelnoRequest: ModifyTelnoRequest): CommonResponse<String> {
        if(jwt.name != modifyTelnoRequest.id) return CommonResponse("token id와 id가 일치하지 않습니다.")

        val findUser = userRepository.findByUserId(modifyTelnoRequest.id)
            ?: throw CustomizedException("유저 정보가 없습니다.", Response.Status.NOT_FOUND)

        findUser.telno = modifyTelnoRequest.modifyTelno

        return CommonResponse("${findUser.userNm}님의 핸드폰번호가 수정되었습니다.")
    }

    /**
     * 사용자 아이디 중복 체크
     * @param userId
     */
    @Transactional
    fun checkUserId(userId: String): Pair<Boolean, String> {
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
    fun login(loginRequest: LoginRequest): CommonResponse<String>{
        var findUser = userRepository.findByUserId(loginRequest.id)
            ?: throw CustomizedException("등록된 ID가 없습니다.", Response.Status.NOT_FOUND)

        return when {
            findUser.pwErrCnt!! >= 5 -> {
                throw CustomizedException("비밀번호 불일치 5회 발생", Response.Status.FORBIDDEN)
            }
            findUser.pw.equals(loginRequest.pw) -> {
                findUser.pwErrCnt = 0
                CommonResponse(TokenUtils.generateUserToken(findUser.id!!))
            }
            else -> {
                findUser.pwErrCnt = findUser.pwErrCnt!! + 1
                throw CustomizedException("사용자 정보가 일치하지 않습니다.", Response.Status.BAD_REQUEST)
            }
        }
    }

    /**
     * 아이디 찾기
     */
    @Transactional
    fun findId(infoUser: InfoUser): CommonResponse<String?> {
        val findUser = userRepository.findId(infoUser)
            ?: throw CustomizedException("등록된 ID가 없습니다.", Response.Status.NOT_FOUND)

        return CommonResponse(findUser.id)
    }

    /**
     * 인증번호 확인
     */
    @Transactional
    fun checkCertNo(checkCertNoRequest: CheckCertNoRequest): CommonResponse<String> {
        val findCert = certRepository.findById(checkCertNoRequest.phoneNo) ?: throw NotFoundException("FAIL")

        return when {
            findCert.expiresDttm.isAfter(Instant.now()) && findCert.certNo == checkCertNoRequest.certNo -> {
                certRepository.delete(findCert)
                CommonResponse("SUCCESS")
            }
            else -> {
                throw CustomizedException("유효하지 않은 인증번호입니다.", Response.Status.NOT_ACCEPTABLE)
            }
        }
    }

    @Transactional
    fun modifyInfo(infoUser: InfoUser) : CommonResponse<String> {
        if(jwt.name != infoUser.id!!) return CommonResponse("token id와 id가 일치하지 않습니다.")

        var findUser = userRepository.findByUserId(infoUser.id!!)
            ?: throw CustomizedException("등록된 ID가 없습니다.", Response.Status.NOT_FOUND)

        findUser.jobCd = infoUser.jobCd
        findUser.ocpCd = infoUser.ocpCd
        findUser.ptTypeCd = infoUser.ptTypeCd
        findUser.instTypeCd = infoUser.instTypeCd
        findUser.instId = infoUser.instId
        findUser.instNm = findUser.instNm
        findUser.dutyDstr1Cd = findUser.dutyDstr1Cd
        findUser.dutyDstr2Cd = findUser.dutyDstr2Cd
        findUser.dutyAddr = findUser.dutyAddr
        findUser.attcId = findUser.attcId
        findUser.updtUserId = infoUser.id

        return CommonResponse("SUCCESS")

    }

}