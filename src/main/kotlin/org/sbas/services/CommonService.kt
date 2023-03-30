package org.sbas.services

import io.quarkus.cache.CacheKey
import io.quarkus.cache.CacheResult
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.dtos.*
import org.sbas.entities.base.BaseAttc
import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeEgen
import org.sbas.entities.base.BaseCodeId
import org.sbas.handlers.FileHandler
import org.sbas.repositories.BaseAttcRepository
import org.sbas.repositories.BaseCodeEgenRepository
import org.sbas.repositories.BaseCodeRepository
import org.sbas.responses.CommonResponse
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.NotFoundException


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
    private lateinit var egenCodeRepository: BaseCodeEgenRepository

    @Inject
    lateinit var repo1: BaseAttcRepository

    @Inject
    private lateinit var handler1: FileHandler

    /**
     * E-GEN 공통코드 목록 조회
     * @param cmMid 대분류 코드
     */
    @Transactional
    @CacheResult(cacheName = "cmMid")
    fun findEgenCodeByCmMid(cmMid: String): List<BaseCodeEgen> {
        return egenCodeRepository.find("cm_mid = ?1", cmMid).list()
    }

    /**
     * 공통코드 목록 조회
     * @param cdGrpId 코드 그룹 ID
     */
    @Transactional
    @CacheResult(cacheName = "cdGrpId")
    fun findBaseCodeByCdGrpId(@CacheKey cdGrpId: String): List<BaseCode> {
        return baseCodeRepository.findBaseCodeByCdGrpId(cdGrpId = cdGrpId)
    }

    /**
     * 시/도 목록 조회
     */
    @Transactional
    @CacheResult(cacheName = "sido")
    fun findSidos(): List<BaseCode> {
        return baseCodeRepository.find("cd_grp_id = 'SIDO'").list()
    }

    /**
     * 시/군/구 목록 조회
     * @param cdGrpId 시/도의 코드 그룹 ID ex) SIDO11, SIDO26...
     */
    @Transactional
    @CacheResult(cacheName = "cdGrpId")
    fun findGuguns(@CacheKey cdGrpId: String): List<BaseCode> {
        return when {
            cdGrpId.matches(Regex("^(SIDO)\\d+")) -> baseCodeRepository.find("cd_grp_id", cdGrpId).list()
            else -> throw NotFoundException("$cdGrpId not found")
        }
    }

    /**
     * 공통코드 등록
     * @param bascCodeSaveReq
     */
    @Transactional
    fun saveBaseCode(bascCodeSaveReq: BascCodeSaveReq) {
        baseCodeRepository.persist(bascCodeSaveReq.toEntity())
    }

    /**
     * 공통코드 수정
     */
    @Transactional
    fun updateBaseCode(baseCodeUpdateReq: BaseCodeUpdateReq): BaseCodeId {
        val baseCodeId = baseCodeUpdateReq.getId()
        val findBaseCode = baseCodeRepository.findById(baseCodeId) ?: throw NotFoundException("$baseCodeId Not found")
        return baseCodeUpdateReq.update(findBaseCode)
    }

    /**
     * 공통코드 삭제
     */
    @Transactional
    fun deleteBaseCode(baseCodeUpdateReq: BaseCodeUpdateReq): Boolean {
        val baseCodeId = baseCodeUpdateReq.getId()
        return baseCodeRepository.deleteById(baseCodeId)
    }

    @Transactional
    fun findBaseCode(): List<BaseCode> {
        //TODO 그룹만 나오게 변경 필요
        return baseCodeRepository.findAll().list()
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
    fun fileUpload(param1: String, param2: FileUpload): CommonResponse<String?> {
        val fileName = handler1.createPublicFile(param2)

        val dotPos = fileName!!.filename.lastIndexOf(".")
        val fileExt = fileName!!.filename.substring(dotPos + 1).lowercase()

        val fileTypeCd = if(fileExt == "bmp" || fileExt == "jpeg" || fileExt == "jpg"
            || fileExt == "gif" || fileExt == "png" || fileExt == "pdf"){
            "FLTP0001"
        }else {
            "FLTP0002"
        }

        val result = fileName!!.toEntity(fileTypeCd, null)

        repo1.persist(result)

        return CommonResponse(result.attcId)
    }
}