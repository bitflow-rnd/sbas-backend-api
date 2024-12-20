package org.sbas.services

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.constants.SbasConst
import org.sbas.dtos.AttcIdResponse
import org.sbas.entities.base.BaseAttc
import org.sbas.handlers.FileHandler
import org.sbas.repositories.BaseAttcRepository
import org.sbas.responses.CommonListResponse
import org.sbas.responses.CommonResponse
import org.sbas.responses.messages.FileResponse
import org.sbas.utils.CustomizedException
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

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
  fun publicFileUpload(param1: String?, param2: MutableList<FileUpload>?): CommonResponse<AttcIdResponse> {
    if (param2.isNullOrEmpty() || param2.any { it.size() == 0L }) {
      throw CustomizedException("파일을 등록하세요.", Response.Status.BAD_REQUEST)
    }

    val attcGrpId = baseAttcRepository.getNextValAttcGrpId()
    val baseAttcList = param2.map {
      val attcId = baseAttcRepository.getNextValAttcId()
      val fileDto = fileHandler.createPublicFile(it)
      val fileTypeCd = getFileTypeCd(fileDto.fileExt)
      val baseAttc =
        fileDto.toPublicEntity(attcGrpId = attcGrpId, attcId = attcId, fileTypeCd = fileTypeCd, rmk = param1)
      baseAttc
    }
    baseAttcRepository.persist(baseAttcList)
    val attcIdList = baseAttcList.map { it.attcId }

    return CommonResponse(AttcIdResponse(attcGrpId, attcIdList))
  }

  @Transactional
  fun privateFileUpload(param1: String?, param2: MutableList<FileUpload>?): CommonResponse<AttcIdResponse> {
    if (param2.isNullOrEmpty() || param2.any { it.size() == 0L }) {
      throw CustomizedException("파일을 등록하세요.", Response.Status.BAD_REQUEST)
    }

    val attcGrpId = baseAttcRepository.getNextValAttcGrpId()
    val baseAttcList = param2.map {
      val attcId = baseAttcRepository.getNextValAttcId()
      val fileDto = fileHandler.createPrivateFile(it)
      val fileTypeCd = getFileTypeCd(fileDto.fileExt)
      val baseAttc =
        fileDto.toPrivateEntity(attcGrpId = attcGrpId, attcId = attcId, fileTypeCd = fileTypeCd, rmk = param1)
      baseAttc
    }
    baseAttcRepository.persist(baseAttcList)
    val attcIdList = baseAttcList.map { it.attcId }

    return CommonResponse(AttcIdResponse(attcGrpId, attcIdList))
  }

  @Transactional
  fun privateFileUploadToGroupId(attcGrpId: String, param1: String?, param2: MutableList<FileUpload>?): CommonResponse<AttcIdResponse> {
    if (param2.isNullOrEmpty() || param2.any { it.size() == 0L }) {
      throw CustomizedException("파일을 등록하세요.", Response.Status.BAD_REQUEST)
    }

    val baseAttcList = param2.map {
      val attcId = baseAttcRepository.getNextValAttcId()
      val fileDto = fileHandler.createPrivateFile(it)
      val fileTypeCd = getFileTypeCd(fileDto.fileExt)
      val baseAttc =
        fileDto.toPrivateEntity(attcGrpId = attcGrpId, attcId = attcId, fileTypeCd = fileTypeCd, rmk = param1)
      baseAttc
    }

    baseAttcRepository.persist(baseAttcList)
    val attcIdList = baseAttcList.map { it.attcId }

    return CommonResponse(AttcIdResponse(attcGrpId, attcIdList))
  }

  private fun getFileTypeCd(fileExt: String): String {
    val imageExtensions = setOf("bmp", "jpeg", "jpg", "gif", "png", "pdf")
    val videoExtensions = setOf("mp4", "avi", "mkv", "wmv", "flv", "mov", "webm", "3gp", "mpeg", "mpg", "ts")

    return when (fileExt.lowercase()) {
      in imageExtensions -> SbasConst.FileTypeCd.IMAGE
      in videoExtensions -> SbasConst.FileTypeCd.VIDEO
      else -> SbasConst.FileTypeCd.ETC
    }
  }

  @Transactional
  fun getImage(attcId: String): CommonResponse<FileResponse> {
    val findFile = baseAttcRepository.findByAttcId(attcId) ?: throw NotFoundException("not found")

    val fileAccessType = when (findFile.privYn) {
      "Y" -> "public"
      "N" -> "private"
      else -> null
    }

    val response =
      FileResponse(findFile.fileTypeCd, "$serverdomain/$fileAccessType${findFile.uriPath}/${findFile.fileNm}")

    return CommonResponse(response)
  }

  fun findPrivateImage(attcId: String): ByteArray {
    val findFile = baseAttcRepository.findByAttcId(attcId) ?: throw NotFoundException("not found")

    require(findFile.privYn == "Y") { "not private file, check attcId" }
    require(findFile.fileTypeCd == SbasConst.FileTypeCd.IMAGE) { "not image file, check attcId" }

    val filePath: Path = Paths.get("${findFile.loclPath}/${findFile.fileNm}")

    return Files.readAllBytes(filePath)
  }

  fun findPrivateImages(attcGrpId: String): CommonListResponse<String> {
    val findFiles = baseAttcRepository.findFilesByAttcGrpId(attcGrpId) ?: throw NotFoundException("not found")

    val imageList = mutableListOf<String>()

    findFiles.forEach {
      require(it.privYn == "Y") { "not private file, check attcId" }
      require(it.fileTypeCd == SbasConst.FileTypeCd.IMAGE) { "not image file, check attcId" }
      val filePath: Path = Paths.get("${it.loclPath}/${it.fileNm}")

      val fileBytes = Files.readAllBytes(filePath)

      val base64String = Base64.getEncoder().encodeToString(fileBytes)

      imageList.add(base64String)
    }

    return CommonListResponse(imageList, count = imageList.size)
  }

  @Transactional
  fun publicFileDownload(attcGrpId: String, attcId: String): Response {
    val baseAttc =
      baseAttcRepository.findByAttcGrpIdAndAttcId(attcGrpId, attcId) ?: throw NotFoundException("baseAttc not found")

    val filePath = "${baseAttc.loclPath}/${baseAttc.fileNm}"
    //        val filePath = "/public${baseAttc.uriPath}/${baseAttc.fileNm}"
    val file = File(filePath)
    log.debug(file)

    log.debug("file exists >>> ${file.exists()}")
    log.debug("file canRead >>> ${file.canRead()}")
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
