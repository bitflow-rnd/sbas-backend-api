package org.sbas.services

import org.jboss.logging.Logger
import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeId
import org.sbas.repositories.BaseCodeRepository
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class CommonService {

    @Inject
    lateinit var log: Logger

    @Inject
    private lateinit var baseCodeRepository: BaseCodeRepository

    @Transactional
    fun findBaseCode(): List<BaseCode> {
        return baseCodeRepository.findAll().list()
    }

    @Transactional
    fun delCodeGrps(baseCodeId: BaseCodeId) {
        val findBaseCode = baseCodeRepository.findById(baseCodeId)
        if (findBaseCode != null) {
            baseCodeRepository.delete(findBaseCode)
        }
    }
}