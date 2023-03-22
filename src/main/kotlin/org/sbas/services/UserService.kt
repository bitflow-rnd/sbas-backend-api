package org.sbas.services

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.constants.SbasConst
import org.sbas.entities.info.InfoUser
import org.sbas.parameters.SmsSendRequest
import org.sbas.parameters.UserRequest
import org.sbas.repositories.UserInfoRepository
import org.sbas.response.StringResponse
import org.sbas.response.UserInfoListResponse
import org.sbas.restclients.NaverSensRestClient
import org.sbas.restparameters.NaverSmsMsgApiParams
import org.sbas.restparameters.NaverSmsReqMsgs
import org.sbas.utils.CypherUtils
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
        infoUser.pw = CypherUtils.crypto(infoUser.pw!!)

        infoUser.rgstUserId = infoUser.id
        infoUser.updtUserId = infoUser.id

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

}