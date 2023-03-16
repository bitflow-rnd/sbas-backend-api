package org.sbas.services

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.json.JSONObject
import org.json.XML
import org.sbas.constants.EgenConst
import org.sbas.entities.base.BaseCodeEgen
import org.sbas.entities.base.BaseCodeEgenId
import org.sbas.entities.base.BaseCodeId
import org.sbas.entities.info.InfoUser
import org.sbas.parameters.BaseCodeRequest
import org.sbas.repositories.BaseCodeEgenRepository
import org.sbas.repositories.BaseCodeRepository
import org.sbas.repositories.TestUserRepository
import org.sbas.response.BaseCodeResponse
import org.sbas.response.EgenCodeMastResponse
import org.sbas.response.EgenHsptMdcncResponse
import org.sbas.restclients.EgenRestClient
import org.sbas.restclients.NaverSensRestClient
import org.sbas.restparameters.NaverSmsMsgApiParams
import org.sbas.restparameters.NaverSmsReqMsgs
import org.sbas.restresponses.EgenCodeMastApiResponse.CodeMastBody.CodeMastItems.CodeMastItem
import org.sbas.restresponses.EgenHsptMdcncApiResponse.HsptMdcncBody.HsptMdcncItems.HsptlMdcncItem
import org.sbas.utils.TokenUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.core.SecurityContext


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class UserService {

    @Inject
    lateinit var log: Logger

    @RestClient
    lateinit var naverSensClient: NaverSensRestClient

    @ConfigProperty(name = "restclient.naversens.serviceid")
    lateinit var naversensserviceid: String

    /**
     * 본인인증 SMS/MMS 메시지 발송
     */
    @Transactional
    fun sendIdentifySms(): BaseCodeResponse {
        val ret = BaseCodeResponse()
        val smsto = NaverSmsReqMsgs("", "", "01088657020")
        naverSensClient.messages(naversensserviceid, NaverSmsMsgApiParams(
            "SMS", null, "01088657020", null, null,
            "", null, null, null,
            listOf(smsto),
            null))
        return ret
    }
    
}