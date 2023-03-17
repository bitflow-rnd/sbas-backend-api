package org.sbas.services

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.constants.SbasConst
import org.sbas.entities.info.InfoUser
import org.sbas.repositories.UserInfoRepository
import org.sbas.response.BaseCodeResponse
import org.sbas.response.StringResponse
import org.sbas.restclients.NaverSensRestClient
import org.sbas.restparameters.NaverSmsMsgApiParams
import org.sbas.restparameters.NaverSmsReqMsgs
import org.sbas.utils.CypherUtils
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
    fun reg(infoUser: InfoUser): StringResponse {
        infoUser.pw = CypherUtils.crypto(infoUser.pw!!)

        infoUser.rgstUserId = "method76"
        infoUser.updtUserId = "method76"

        repository.persist(infoUser)

        return StringResponse(infoUser.id)
    }

    /**
     * 본인인증 SMS/MMS 메시지 발송
     */
    @Transactional
    fun sendIdentifySms(): BaseCodeResponse {
        val ret = BaseCodeResponse()

        val smsto = NaverSmsReqMsgs("", "", "01082072505")

        naverSensClient.messages(naversensserviceid, NaverSmsMsgApiParams(
            SbasConst.MsgType.SMS, null, SbasConst.MSG_SEND_NO, null, "COMM",
            "안녕하세요", null, null, null,
            listOf(smsto),
            null)
        )

        return ret
    }



}