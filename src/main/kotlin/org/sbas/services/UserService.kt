package org.sbas.services

import org.sbas.entities.BaseCodeId
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
    fun getBaseCode(param1: BaseCodeId): BaseCodeResponse {
        val res1 = repo1.findById(param1)
        val ret = BaseCodeResponse()
        return ret
    }
    
}