package org.sbas.services

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
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
class TestUserService {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var jwt: JsonWebToken

    @Inject
    lateinit var repo1: BaseCodeRepository

    @Inject
    lateinit var repo2: BaseCodeEgenRepository

    @Inject
    lateinit var userRepo: TestUserRepository

    @RestClient
    lateinit var egenapi: EgenRestClient

    @ConfigProperty(name = "restclient.egen.service.key")
    lateinit var serviceKey: String

    @Transactional
    fun getBaseCode(): BaseCodeResponse {
        val param2 = BaseCodeId("12345678", "23456789")
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
    fun getCodeMast(): EgenCodeMastResponse {
        val retlist = mutableListOf<CodeMastItem>()
        val savelist = mutableListOf<BaseCodeEgen>()

        for (cmMid: String in EgenConst.EGEN_GRP_CDS) {
            val res = egenapi.getCodeMastInfo(serviceKey, cmMid)
            if (res.header?.resultCode == EgenConst.SUCCESS) {
                log.debug("SUCCESS")
                //val list = ItemWrapper(res.body?.items?.item!!)
                for (item in res.body?.items?.item!!) {
                    log.debug("item")
                    retlist.add(item)
                    val entity = BaseCodeEgen()
                    entity.id = BaseCodeEgenId()
                    entity.id!!.cmMid = item.cmMid
                    entity.id!!.cmSid = item.cmSid
                    entity.cmMnm = item.cmMnm
                    entity.cmSnm = item.cmSnm
                    entity.rgstUserId = "method76"
                    entity.updtUserId = "method76"
                    savelist.add(entity)
                }
            }
        }
        try {
//            repo2.persist(savelist)
        } catch (e: Exception) {
        } finally {

        }
        return EgenCodeMastResponse(retlist)
    }

    @Transactional
    fun getHosptalMedclinic(): EgenHsptMdcncResponse {
        val retlist = mutableListOf<HsptlMdcncItem>()
        val savelist = mutableListOf<BaseCodeEgen>()
        val res = egenapi.getHsptlMdcncListInfoInqire(serviceKey, "")
        if (res.header?.resultCode == EgenConst.SUCCESS) {
            // 응답성공
            for (item in res.body?.items?.item!!) {
                log.debug("e-gen api res $item.dutyAddr")
                retlist.add(item)
            }
        }
        return EgenHsptMdcncResponse(retlist)
    }

    @Transactional
    fun getBaseCode(param1: BaseCodeRequest?, ctx: SecurityContext): BaseCodeResponse {
        val jwtString = TokenUtils.debugJwtContent(jwt, ctx)
        log.debug("jwtString is $jwtString");
        val param2 = BaseCodeId(param1!!.cdGrpId!!, param1!!.cdId!!)
        val res1 = repo1.findById(param2)
        // some programming logic should be placed here
        val ret = BaseCodeResponse()
        return ret
    }

    @Transactional
    fun login(infoUser: InfoUser): String{
        val findUser = userRepo.findByUserId(infoUser.id!!)

        return if(findUser!!.pw.equals(infoUser.pw)){
            TokenUtils.generateUserToken(findUser.id!!)
        }else {
            "FAIL"
        }
    }

    @Transactional
    fun getUser(): JsonWebToken{
        log.warn("jenkins test~~~~~222")
        return jwt
    }
    
}