package org.sbas.services

import org.sbas.entities.BaseCodeId
import org.sbas.parameters.BaseCodeRequest
import org.sbas.repositories.BaseCodeRepository
import org.sbas.response.BaseCodeResponse
import java.util.concurrent.atomic.AtomicReference
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class UserService {

    @Inject
    private lateinit var repo1: BaseCodeRepository

    @Transactional
    fun getBaseCode(): BaseCodeResponse {
        val param2 = BaseCodeId()
        param2.cdGrpId = "12345678"
        param2.cdId = "23456789"
        val res1 = repo1.findById(param2)
        val ret = BaseCodeResponse()
        return ret
    }

    @Transactional
    fun getBaseCode(param1: BaseCodeRequest?): BaseCodeResponse {
        val param2 = BaseCodeId()
        param2.cdGrpId = param1!!.cdGrpId!!
        param2.cdId = param1!!.cdId!!
        val res1 = repo1.findById(param2)
        val ret = BaseCodeResponse()
        return ret
    }
    
}