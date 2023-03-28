package org.sbas.services

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.constants.SbasConst
import org.sbas.entities.info.InfoUser
import org.sbas.parameters.SmsSendRequest
import org.sbas.parameters.UserRequest
import org.sbas.parameters.modifyPwRequest
import org.sbas.repositories.UserInfoRepository
import org.sbas.responses.StringResponse
import org.sbas.responses.UserInfoListResponse
import org.sbas.restclients.NaverSensRestClient
import org.sbas.restparameters.NaverSmsMsgApiParams
import org.sbas.restparameters.NaverSmsReqMsgs
import org.sbas.utils.TokenUtils
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class UserService {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var repository: UserInfoRepository

    @RestClient
    lateinit var naverSensClient: NaverSensRestClient

    @ConfigProperty(name = "restclient.naversens.serviceid")
    lateinit var naversensserviceid: String

    @Transactional
    fun reqUserReg(infoUser: InfoUser): StringResponse {

        infoUser.rgstUserId = "admin"
        infoUser.updtUserId = "admin"

        repository.persist(infoUser)

        return StringResponse("${infoUser.userNm}님 사용자 등록을 요청하였습니다.")
    }

    /**
     * 백오피스에서 어드민(전산담당)이 처리하는 API
     */
    @Transactional
    fun getUsers(searchData: String): UserInfoListResponse {
        var userInfoListResponse = UserInfoListResponse()
        val findList = repository.findLike(searchData)

        userInfoListResponse.result = findList

        return userInfoListResponse
    }

    /**
     * 본인인증 SMS/MMS 메시지 발송
     */
    @Transactional
    fun sendIdentifySms(smsSendRequest: SmsSendRequest): StringResponse {
        val ret = StringResponse()

        var rand = ""
        for(i: Int in 0..5){
            rand += ('0'..'9').random()
        }
        ret.result = rand

        val smsTo = NaverSmsReqMsgs("", "", smsSendRequest.to!!)

        naverSensClient.messages(naversensserviceid, NaverSmsMsgApiParams(
            SbasConst.MsgType.SMS, null, SbasConst.MSG_SEND_NO, null, "COMM",
            smsSendRequest.name + "님 안녕하세요. 인증번호는 [ $rand ]입니다. 감사합니다.", null, null, null, mutableListOf(smsTo), null)
        )

        return ret
    }

    @Transactional
    fun reg(request: UserRequest): StringResponse {
        var findUser = repository.findByUserId(request.id)

        findUser!!.aprvDttm = Instant.now()
        findUser.statClas = "USER"
        findUser.updtUserId = request.adminId
        findUser.aprvUserId = request.adminId

        return StringResponse("${findUser.userNm}님 사용자 등록을 승인하였습니다.")
    }

    @Transactional
    fun deleteUser(request: UserRequest): StringResponse {
        val findUser = repository.findByUserId(request.id)

        findUser!!.statClas = "DEL"
        findUser.updtUserId = request.adminId

        return StringResponse("${request.id} 계정을 삭제하였습니다.")
    }

    @Transactional
    fun modifyPw(modifyPwRequest: modifyPwRequest): StringResponse {
        val response: StringResponse = StringResponse()
        val findUser = repository.findByUserId(modifyPwRequest.id)

        if(findUser != null){
            findUser.pw = modifyPwRequest.modifyPw
            response.result = "SUCCESS"
        }else {
            response.result = "FAIL"
        }

        return response
    }

    /**
     * 사용자 아이디 중복 체크
     * @param userId
     */
    @Transactional
    fun checkUserId(userId: String): Pair<Boolean, String> {
        return when {
            repository.existByUserId(userId) -> Pair(true, "이미 사용중인 아이디입니다.")
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
            repository.existByTelNo(telno) -> Pair(true, "이미 사용중인 번호입니다.")
            else -> Pair(false, "사용 가능한 번호입니다.")
        }
    }

    /**
     * 로그인
     */
    @Transactional
    fun login(infoUser: InfoUser): StringResponse{
        val findUser = repository.findByUserId(infoUser.id!!)

        return if(findUser!!.pw.equals(infoUser.pw)){
            if(findUser.statClas.startsWith("URST")){
                StringResponse(TokenUtils.generateUserToken(findUser.id!!))
            }else {
                StringResponse(TokenUtils.generateAdminToken(findUser.id!!))
            }
        }else {
            StringResponse("사용자 정보가 일치하지 않습니다.")
        }
    }

    /**
     * 아이디 찾기
     */
    @Transactional
    fun findId(infoUser: InfoUser): StringResponse{
        val findUser = repository.findId(infoUser)

        return StringResponse(findUser!!.id)

    }

}