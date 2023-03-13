package org.sbas.services

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.entities.base.BaseCodeId
import org.sbas.parameters.BaseCodeRequest
import org.sbas.repositories.BaseCodeRepository
import org.sbas.response.BaseCodeResponse
import org.sbas.restclients.EgenRestClient
import org.sbas.restresponses.EgenCodeMastItem
import org.sbas.restresponses.EgenResponse
import org.sbas.utils.TokenUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.core.SecurityContext


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class TestUserService {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var jwt: JsonWebToken

    @Inject
    lateinit var repo1: BaseCodeRepository

    @RestClient
    lateinit var egenapi: EgenRestClient

    @ConfigProperty(name = "restclient.egen.service.key")
    lateinit var serviceKey: String

    @Transactional
    fun getBaseCode(): BaseCodeResponse {
        val param2 = BaseCodeId()
        param2.cdGrpId = "12345678"
        param2.cdId = "23456789"
        val res1 = repo1.findById(param2)
        val ret = BaseCodeResponse()
        if (res1?.id != null) {
            ret.cdGrpId = res1.id!!.cdGrpId
            ret.cdId = res1.id!!.cdId
            ret.cdGrpNm = res1.cdGrpNm
            ret.cdNm = res1.cdNm
        }
        return ret
    }

    @Transactional
    fun getCodeMast(): EgenResponse {
        return egenapi.getCodeMast(serviceKey, "P004")
    }

    @Transactional
    fun getBaseCode(param1: BaseCodeRequest?, ctx: SecurityContext): BaseCodeResponse {
        val jwtString = TokenUtils.debugJwtContent(jwt, ctx)
        log.debug("jwtString is $jwtString");
        val param2 = BaseCodeId()
        param2.cdGrpId = param1!!.cdGrpId!!
        param2.cdId = param1!!.cdId!!
        val res1 = repo1.findById(param2)
        // some programming logic should be placed here
        val ret = BaseCodeResponse()
        return ret
    }
    
}