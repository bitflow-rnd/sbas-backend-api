package org.sbas.services

import io.quarkus.cache.CacheKey
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.constants.SbasConst
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
import org.sbas.responses.messages.FileResponse
import org.sbas.utils.CustomizedException
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
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
     * 공통코드 그룹 목록 조회
     */
    @Transactional
    fun findBaseCdGrpList(): CommonResponse<*> {
        return CommonResponse(baseCodeRepository.findBaseCodeGrpList())
    }

    /**
     * 공통코드 그룹 등록
     */
    @Transactional
    fun saveBaseCodeGrp(saveReq: BaseCodeGrpSaveReq): CommonResponse<String> {
        val baseCodeGrp = baseCodeRepository.findBaseCodeGrp(saveReq.cdGrpId)
        if (baseCodeGrp != null) {
            throw CustomizedException("${saveReq.cdGrpId} 이미 등록되어 있습니다.", Response.Status.CONFLICT)
        }
        baseCodeRepository.persist(saveReq.toEntity())

        return CommonResponse("cdGrpId: ${saveReq.cdGrpId}")
    }

    /**
     * 공통코드 그룹 수정
     */
    @Transactional
    fun updateBaseCdGrp(updateReq: BaseCodeGrpUpdateReq): CommonResponse<String> {
        val baseCodeGrp = baseCodeRepository.findBaseCodeGrp(updateReq.cdGrpId) ?: throw NotFoundException("${updateReq.cdGrpId} not found")
        
        // 코드 그룹 항목 수정
        baseCodeGrp.changeBaseCodeGrp(updateReq.cdGrpNm, updateReq.rmk)

        // 같은 코드 그룹 ID인 항목들 변경
        val baseCodeList = baseCodeRepository.findBaseCodeByCdGrpId(updateReq.cdGrpId)
        if (baseCodeList.isNotEmpty()) {
            baseCodeList.forEach { it.changeBaseCodeGrpNm(updateReq.cdGrpNm) }
            return CommonResponse("수정 성공")
        } else {
            throw NotFoundException("${updateReq.cdGrpId} not found")
        }
    }

    /**
     * 공통코드 그룹 삭제
     */
    @Transactional
    fun deleteBaseCdGrp(cdGrpId: String): CommonResponse<String> {
        val baseCodeList = baseCodeRepository.findBaseCodeByCdGrpId(cdGrpId)
        val baseCodeGrp = baseCodeRepository.findBaseCodeGrp(cdGrpId) ?: throw NotFoundException("$cdGrpId not found")
        
        // cdSeq 가 0이 아닌 항목들이 존재하면 삭제 X
        if (baseCodeList.any { it.cdSeq != 0 }) {
            throw CustomizedException("하위 항목들이 존재합니다.", Response.Status.CONFLICT)
        }
        
        baseCodeRepository.delete(baseCodeGrp)
        
        return CommonResponse("삭제 성공")
    }

    /**
     * 공통코드 목록 조회
     */
    @Transactional
//    @CacheResult(cacheName = "cdGrpId")
    fun findBaseCodeList(@CacheKey cdGrpId: String): CommonResponse<List<BaseCodeResponse>> {
        val baseCodeList = baseCodeRepository.findBaseCodeByCdGrpId(cdGrpId = cdGrpId)
        return toBaseCodeResponse(baseCodeList)
    }

    /**
     * 공통코드 등록
     */
    @Transactional
    fun saveBaseCode(saveReq: BaseCodeSaveReq): CommonResponse<String> {
        val findBaseCode = baseCodeRepository.findById(BaseCodeId(saveReq.cdGrpId, saveReq.cdId))
        if (findBaseCode != null) {
            throw CustomizedException("${saveReq.cdId} 이미 등록되어 있습니다.", Response.Status.CONFLICT)
        }
        baseCodeRepository.persist(saveReq.toEntity())

        return CommonResponse("cdId: ${saveReq.cdId}")
    }

    /**
     * 공통코드 수정
     */
    @Transactional
//    @CacheInvalidate(cacheName = "cdGrpId")
    fun updateBaseCode(updateReq: BaseCodeUpdateReq): CommonResponse<String> {
        val baseCodeId = updateReq.getId()
        val findBaseCode = baseCodeRepository.findById(baseCodeId) ?: throw NotFoundException("${baseCodeId.cdId} not found")

        findBaseCode.changeBaseCode(updateReq)

        return CommonResponse("수정 성공")
    }

    /**
     * 공통코드 삭제
     */
    @Transactional
//    @CacheInvalidate(cacheName = "cdGrpId")
    fun deleteBaseCode(cdId: String): CommonResponse<String> {
        val findBaseCode = baseCodeRepository.findBaseCodeByCdId(cdId) ?: throw NotFoundException("$cdId not found")

        baseCodeRepository.delete(findBaseCode)
        
        return CommonResponse("삭제 성공")
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
     * E-GEN 공통코드 목록 조회
     * @param cmMid 대분류 코드
     */
    @Transactional
//    @CacheResult(cacheName = "cmMid")
    fun findCodeEgenList(cmMid: String): CommonResponse<List<BaseCodeEgen>> {
        return CommonResponse(egenCodeRepository.findCodeEgenByCmMid(cmMid = cmMid))
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
    fun publicFileUpload(param1: String, param2: MutableList<FileUpload>): CommonResponse<MutableList<String>> {
        if (Objects.isNull(param2)) {
            throw CustomizedException("파일을 등록하시오.", Response.Status.BAD_REQUEST)
        }

        val attcGrpId = baseAttcRepository.getNextValAttcGrpId()
        log.debug(">>>>>>>>>>>>>>> $attcGrpId")

        val result = mutableListOf<String>()
        param2.forEach {
            val fileDto = fileHandler.createPublicFile(it)

            val dotPos = fileDto.fileName.lastIndexOf(".")
            val fileExt = fileDto.fileName.substring(dotPos + 1).lowercase()

            val fileTypeCd = getFileTypeCd(fileExt)
            val baseAttc = fileDto.toPublicEntity(attcGrpId = attcGrpId, fileTypeCd = fileTypeCd, rmk = null)

            baseAttcRepository.persist(baseAttc)

            result.add(baseAttc.attcId!!)
        }

        return CommonResponse(result)
    }

    @Transactional
    fun privateFileUpload(param1: String, param2: MutableList<FileUpload>): CommonResponse<MutableList<String>> {
        if (Objects.isNull(param2)) {
            throw CustomizedException("파일을 등록하시오.", Response.Status.BAD_REQUEST)
        }

        val attcGrpId = baseAttcRepository.getNextValAttcGrpId()
        log.debug(">>>>>>>>>>>>>>> $attcGrpId")

        val result = mutableListOf<String>()
        param2.forEach {
            val fileDto = fileHandler.createPrivateFile(it)

            val dotPos = fileDto.fileName.lastIndexOf(".")
            val fileExt = fileDto.fileName.substring(dotPos + 1).lowercase()

            val fileTypeCd = getFileTypeCd(fileExt)
            val baseAttc = fileDto.toPrivateEntity(attcGrpId = attcGrpId, fileTypeCd = fileTypeCd, rmk = null)

            baseAttcRepository.persist(baseAttc)

            result.add(baseAttc.attcId!!)
        }

        return CommonResponse(result)
    }

    private fun getFileTypeCd(fileExt: String): String {
        val imageExtensions = setOf("bmp", "jpeg", "jpg", "gif", "png", "pdf")
        val fileTypeCd = if (fileExt.lowercase() in imageExtensions) {
            SbasConst.FileTypeCd.IMAGE
        } else {
            SbasConst.FileTypeCd.VIDEO
        }
        return fileTypeCd
    }

    @Transactional
    fun getImage(attcId: String): CommonResponse<FileResponse> {
        val findFile = baseAttcRepository.findByAttcId(attcId) ?: throw NotFoundException("not found")

        val response: FileResponse = FileResponse(findFile.fileTypeCd, "$serverdomain${findFile.uriPath}/${findFile.fileNm}")

        return CommonResponse(response)
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
    private fun toBaseCodeResponse(baseCodeList: List<BaseCode>) =
        CommonResponse(baseCodeList.map {
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