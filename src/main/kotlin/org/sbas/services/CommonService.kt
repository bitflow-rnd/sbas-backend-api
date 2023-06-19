package org.sbas.services

import io.quarkus.cache.CacheInvalidate
import io.quarkus.cache.CacheKey
import io.quarkus.cache.CacheResult
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.constants.SbasConst
import org.sbas.dtos.*
import org.sbas.entities.base.BaseAttc
import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeEgen
import org.sbas.handlers.FileHandler
import org.sbas.repositories.BaseAttcRepository
import org.sbas.repositories.BaseCodeEgenRepository
import org.sbas.repositories.BaseCodeRepository
import org.sbas.responses.CommonResponse
import org.sbas.responses.messages.FileResponse
import org.sbas.utils.CustomizedException
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.NotFoundException
import javax.ws.rs.core.Response


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
    private lateinit var baseAttcRepository: BaseAttcRepository

    @Inject
    private lateinit var fileHandler: FileHandler

    @ConfigProperty(name = "domain.this")
    private lateinit var serverdomain: String

    /**
     * E-GEN 공통코드 목록 조회
     * @param cmMid 대분류 코드
     */
    @Transactional
//    @CacheResult(cacheName = "cmMid")
    fun findCodeEgenList(cmMid: String): CommonResponse<List<BaseCodeEgen>> {
        return CommonResponse(egenCodeRepository.findCodeEgenByCmMid(cmMid = cmMid))
    }

    /**
     * 공통코드 목록 조회
     * @param cdGrpId 코드 그룹 ID
     */
    @Transactional
//    @CacheResult(cacheName = "cdGrpId")
    fun findBaseCodeList(@CacheKey cdGrpId: String): CommonResponse<List<BaseCodeResponse>> {
        val findBaseCodeList = baseCodeRepository.findBaseCodeByCdGrpId(cdGrpId = cdGrpId)
        return toBaseCodeResponse(findBaseCodeList)
    }

    /**
     * 시/도 목록 조회
     */
    @Transactional
//    @CacheResult(cacheName = "sido")
    fun findSidoList(): CommonResponse<List<BaseCodeResponse>> {
        val findBaseCodeList = baseCodeRepository.findBaseCodeByCdGrpId("SIDO")
        return toBaseCodeResponse(findBaseCodeList)
    }

    /**
     * 시/군/구 목록 조회
     * @param cdGrpId 시/도의 코드 그룹 ID ex) SIDO11, SIDO26...
     */
    @Transactional
//    @CacheResult(cacheName = "cdGrpId")
    fun findGugunList(@CacheKey cdGrpId: String): CommonResponse<List<BaseCodeResponse>> {
        return when {
            cdGrpId.matches(Regex("^(SIDO)\\d+")) -> {
                val findBaseCodeList = baseCodeRepository.findBaseCodeByCdGrpId(cdGrpId = cdGrpId)
                toBaseCodeResponse(findBaseCodeList)
            }
            else -> throw NotFoundException("${cdGrpId}는 시/도의 코드 그룹 ID가 아닙니다.")
        }
    }

    /**
     * 공통코드 그룹 목록
     */
    @Transactional
    fun findBaseCdGrpList(): CommonResponse<*> {
        return CommonResponse(baseCodeRepository.findBaseCdGrpList())
    }

    /**
     * 공통코드 그룹 수정
     */
    @Transactional
    fun updateBaseCdGrp(updateReq: BaseCodeUpdateReq): CommonResponse<String> {
        val baseCodeList = baseCodeRepository.findBaseCodeByCdGrpId(updateReq.cdGrpId)
        if (baseCodeList.isNotEmpty()) {
            baseCodeList.forEach { it.updateBaseCdGrp(updateReq.cdGrpNm!!) }
            return CommonResponse("성공")
        } else {
            throw NotFoundException("${updateReq.cdGrpId} not found")
        }
    }

    /**
     * 공통코드 그룹 삭제
     */
    @Transactional
    fun deleteBaseCdGrp(cdGrpId: String): CommonResponse<String> {
        val findBaseCode = baseCodeRepository.findBaseCodeByCdGrpId(cdGrpId)
        if (findBaseCode.isNotEmpty()) {
            findBaseCode.forEach{ baseCodeRepository.delete(it) }
            return CommonResponse("삭제 성공")
        }
        return CommonResponse("삭제 실패")
    }

    /**
     * 공통코드 등록
     */
    @Transactional
    fun saveBaseCode(saveReq: BaseCodeSaveReq): CommonResponse<BaseCode?> {
        val findById = baseCodeRepository.findById(saveReq.toCdIdEntity().id)
        when (findById) {
            null -> {
                baseCodeRepository.persist(saveReq.toCdIdEntity())
                saveReq.toCdIdEntity()
            }
        }
        return CommonResponse(findById)
    }

    /**
     * 공통코드 수정
     */
    @Transactional
//    @CacheInvalidate(cacheName = "cdGrpId")
    fun updateBaseCode(updateReq: BaseCodeUpdateReq): CommonResponse<String> {
        val baseCodeId = updateReq.getId()
        val findBaseCode = baseCodeRepository.findById(baseCodeId) ?: throw NotFoundException("$baseCodeId Not found")
        findBaseCode.updateBaseCode(updateReq)
        return CommonResponse("수정 성공")
    }

    /**
     * 공통코드 삭제
     */
    @Transactional
//    @CacheInvalidate(cacheName = "cdGrpId")
    fun deleteBaseCode(updateReq: BaseCodeUpdateReq): CommonResponse<Boolean> {
        val baseCodeId = updateReq.getId()
        return CommonResponse(baseCodeRepository.deleteById(baseCodeId))
    }

    /**
     * 비공개 권한 파일그룹 목록 리턴
     */
    @Transactional
    fun findFiles(attcGrpId: String): List<BaseAttc> {
        return baseAttcRepository.list("attc_grp_id", attcGrpId)
    }

    /**
     * 전체 공개 권한 파일 업로드
     */
    @Transactional
    fun publicFileUpload(param1: String, param2: FileUpload): CommonResponse<String?> {
        if(param2.fileName()=="") throw CustomizedException("파일을 등록하시오.", Response.Status.BAD_REQUEST)

        val fileName = fileHandler.createPublicFile(param2)

        val dotPos = fileName!!.filename.lastIndexOf(".")
        val fileExt = fileName.filename.substring(dotPos + 1).lowercase()

        val fileTypeCd = if(fileExt == "bmp" || fileExt == "jpeg" || fileExt == "jpg"
            || fileExt == "gif" || fileExt == "png" || fileExt == "pdf"){
            SbasConst.FileTypeCd.IMAGE
        }else {
            SbasConst.FileTypeCd.VIDEO
        }

        val result = fileName.toEntity(fileTypeCd, null)

        log.warn(result)

        baseAttcRepository.persist(result)

        return CommonResponse(result.attcId)
    }

    @Transactional
    fun getImage(attcId: String): CommonResponse<FileResponse> {
        val findFile = baseAttcRepository.findByAttcId(attcId) ?: throw NotFoundException("not found")

        val response: FileResponse = FileResponse(findFile.fileTypeCd, "$serverdomain${findFile.uriPath}/${findFile.fileNm}")

        return CommonResponse(response)
    }

    @Transactional
    fun privateFileUpload(param1: String, param2: FileUpload): CommonResponse<String?> {
        val fileName = fileHandler.createPrivateFile(param2)

        return CommonResponse(fileName)
    }

    @Transactional
    fun publicFileDownload(attcGrpId: String, attcId: String): Response {
        val baseAttc = baseAttcRepository.findByAttcGrpIdAndAttcId(attcGrpId, attcId) ?: throw NotFoundException("baseAttc not found")

        val filePath = "${baseAttc.loclPath}/${baseAttc.fileNm}"
//        val filePath = "${baseAttc.uriPath}/${baseAttc.fileNm}"
        val file = File(filePath)
        log.debug(file)

        // 파일이 존재하거나 읽을 수 있을 때
        if (file.exists() && file.canRead()) {
            try {
                // 파일 스트림 생성
                val inputStream: InputStream = FileInputStream(file)
                // 파일 다운로드 응답 생성
                return Response.ok(inputStream)
                    .header("Content-Disposition", "attachment; filename=\"" + file.name + "\"")
                    .build()
            } catch (e: IOException) {
                throw CustomizedException("internal server error", Response.Status.INTERNAL_SERVER_ERROR)
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build()
    }

    /**
     * BaseCode -> BaseCodeResponse 변환
     */
    private fun toBaseCodeResponse(findBaseCodeList: List<BaseCode>) =
        CommonResponse(findBaseCodeList.map {
            BaseCodeResponse(
                cdGrpId = it.id.cdGrpId,
                cdGrpNm = it.cdGrpNm,
                cdId = it.id.cdId,
                cdNm = it.cdNm,
                cdVal = it.cdVal,
                cdSeq = it.cdSeq,
                rmk = it.rmk,
            )
        }.toList())
}