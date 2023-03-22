package org.sbas.services

import io.quarkus.cache.CacheInvalidate
import io.quarkus.cache.CacheKey
import io.quarkus.cache.CacheResult
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.entities.base.BaseAttc
import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeId
import org.sbas.handlers.FileHandler
import org.sbas.repositories.BaseAttcRepository
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

    @Inject
    lateinit var repo1: BaseAttcRepository

    @Inject
    private lateinit var handler1: FileHandler

    @Transactional
    fun findBaseCode(): List<BaseCode> {
        return baseCodeRepository.findAll().list()
    }

    @Transactional
    @CacheResult(cacheName = "cdGrpId")
    fun findBaseCodeByCdGrpId(@CacheKey cdGrpId: String): List<BaseCode> {
        return baseCodeRepository.findBaseCodeByCdGrpId(cdGrpId = cdGrpId)
    }

    @Transactional
    @CacheResult(cacheName = "sido")
    fun findSidos(): List<BaseCode> {
        return baseCodeRepository.find("cd_grp_id = 'SIDO'").list()
    }

    @Transactional
    @CacheResult(cacheName = "cdGrpId")
    fun findGuguns(@CacheKey cdGrpId: String): List<BaseCode> {
        //TODO 공통코드 조회랑 같음 -> 쿼리 변경
        return baseCodeRepository.find("cd_grp_id = ?1", cdGrpId).list()
    }

    @Transactional
    fun delCodeGrps(baseCodeId: BaseCodeId) {
        val findBaseCode = baseCodeRepository.findById(baseCodeId)
        if (findBaseCode != null) {
            baseCodeRepository.delete(findBaseCode)
        }
    }

    /**
     * 비공개 권한 파일그룹 목록 리턴
     */
    @Transactional
    fun findFiles(attcGrpId: String): List<BaseAttc> {
        return repo1.list("attc_grp_id", attcGrpId)
    }

    /**
     * 전체 공개 권한 파일 업로드
     */
    @Transactional
    fun fileUpload(param1: String, param2: FileUpload) {
        val fileName = handler1.createPublicFile(param2)
    }

}