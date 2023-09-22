package org.sbas.services

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.constants.SbasConst
import org.sbas.entities.base.BaseAttc
import org.sbas.handlers.FileHandler
import org.sbas.repositories.BaseAttcRepository
import org.sbas.responses.CommonResponse
import org.sbas.responses.CommonListResponse
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

@ApplicationScoped
class FileService {

    @Inject
    private lateinit var log: Logger

    @Inject
    private lateinit var baseAttcRepository: BaseAttcRepository

    @Inject
    private lateinit var fileHandler: FileHandler

    @ConfigProperty(name = "domain.this")
    private lateinit var serverdomain: String

    /**
     * 비공개 권한 파일그룹 목록 리턴
     */
    @Transactional
    fun findFiles(attcGrpId: String): CommonResponse<List<BaseAttc>> {
        val files = baseAttcRepository.findFilesByAttcGrpId(attcGrpId)
        return CommonResponse(files)
    }

    /**
     * 전체 공개 권한 파일 업로드
     */
    @Transactional
    fun publicFileUpload(param1: String?, param2: MutableList<FileUpload>?): CommonListResponse<String> {
        if (param2.isNullOrEmpty()) {
            throw CustomizedException("파일을 등록하세요.", Response.Status.BAD_REQUEST)
        }

        val result = mutableListOf<String>()
        var attcGrpId = ""
        param2.forEach {
            if (it.size() == 0L) {
                throw CustomizedException("파일을 등록하세요.", Response.Status.BAD_REQUEST)
            }
            if(attcGrpId == ""){
                attcGrpId = baseAttcRepository.getNextValAttcGrpId()
            }

            val fileDto = fileHandler.createPublicFile(it)

            val dotPos = fileDto.fileName.lastIndexOf(".")
            val fileExt = fileDto.fileName.substring(dotPos + 1).lowercase()

            val fileTypeCd = getFileTypeCd(fileExt)
            val baseAttc = fileDto.toPublicEntity(attcGrpId = attcGrpId, fileTypeCd = fileTypeCd, rmk = null)

            baseAttcRepository.persist(baseAttc)

            result.add(baseAttc.attcId)
        }

        return CommonListResponse(result)
    }

    @Transactional
    fun privateFileUpload(param1: String?, param2: MutableList<FileUpload>?): CommonListResponse<String> {
        if (param2.isNullOrEmpty()) {
            throw CustomizedException("파일을 등록하세요.", Response.Status.BAD_REQUEST)
        }

        val result = mutableListOf<String>()
        var attcGrpId = ""
        param2.forEach {
            if (it.size() == 0L) {
                throw CustomizedException("파일을 등록하세요.", Response.Status.BAD_REQUEST)
            }
            if(attcGrpId == ""){
                attcGrpId = baseAttcRepository.getNextValAttcGrpId()
            }
            val fileDto = fileHandler.createPrivateFile(it)

            val dotPos = fileDto.fileName.lastIndexOf(".")
            val fileExt = fileDto.fileName.substring(dotPos + 1).lowercase()

            val fileTypeCd = getFileTypeCd(fileExt)
            val baseAttc = fileDto.toPrivateEntity(attcGrpId = attcGrpId, fileTypeCd = fileTypeCd, rmk = null)

            baseAttcRepository.persist(baseAttc)

            result.add(baseAttc.attcId)
        }

        return CommonListResponse(result)
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

        val fileAccessType = when (findFile.privYn) {
            "Y" -> "public"
            "N" -> "private"
            else -> null
        }

        val response = FileResponse(findFile.fileTypeCd, "$serverdomain/$fileAccessType${findFile.uriPath}/${findFile.fileNm}")

        return CommonResponse(response)
    }

    @Transactional
    fun publicFileDownload(attcGrpId: String, attcId: String): Response {
        val baseAttc = baseAttcRepository.findByAttcGrpIdAndAttcId(attcGrpId, attcId) ?: throw NotFoundException("baseAttc not found")

//        val filePath = "${baseAttc.loclPath}/${baseAttc.fileNm}"
        val filePath = "public${baseAttc.uriPath}/${baseAttc.fileNm}"
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
                throw CustomizedException("파일 다운로드 에러 발생", Response.Status.INTERNAL_SERVER_ERROR)
            }
        } else {
            throw NotFoundException("file not found")
        }
    }
}
