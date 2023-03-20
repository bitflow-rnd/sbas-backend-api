package org.sbas.handlers

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.json.JSONObject
import org.json.XML
import org.sbas.constants.EgenConst
import org.sbas.constants.SbasConst
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
import org.sbas.restclients.NaverSensRestClient
import org.sbas.restparameters.NaverSmsMsgApiParams
import org.sbas.restparameters.NaverSmsReqMsgs
import org.sbas.restresponses.EgenCodeMastApiResponse.CodeMastBody.CodeMastItems.CodeMastItem
import org.sbas.restresponses.EgenHsptMdcncApiResponse.HsptMdcncBody.HsptMdcncItems.HsptlMdcncItem
import org.sbas.utils.TokenUtils
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.core.SecurityContext
import kotlin.io.path.readBytes


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class FileHandler {

    @Inject
    lateinit var log: Logger

    @ConfigProperty(name = "upload.public.dir")
    lateinit var UPLOAD_DIR_PUBLIC: String

    @ConfigProperty(name = "upload.private.dir")
    lateinit var UPLOAD_DIR_PRIVATE: String

    /**
     * 전체 공개 권한 파일 업로드
     */
    fun createPublicFile(param: FileUpload): String? {
        val now = LocalDateTime.now()
        val format = DateTimeFormatter.ofPattern("yyyyMM")
        val fileName = Instant.now().toEpochMilli()
        val path = UPLOAD_DIR_PUBLIC + "/" + now.format(format)
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val dotPos = param.fileName().lastIndexOf(".")
        val fileExt = param.fileName().substring(dotPos + 1)
        val file = File("$path/$fileName.${fileExt.lowercase()}")
        val created = file.createNewFile()
        return if (created) {
            file.writeBytes(param.uploadedFile().readBytes())
            log.debug("file uploaded at ${file.absolutePath}")
            "$fileName.${fileExt.lowercase()}"
        } else {
            null
        }
    }

    /**
     * 사용자 공개 권한 파일 업로드
     */
    fun createPrivateFile(param: FileUpload): String? {
        val now = LocalDateTime.now()
        val format = DateTimeFormatter.ofPattern("yyyyMM")
        val fileName = Instant.now().toEpochMilli()
        val path = UPLOAD_DIR_PRIVATE + "/" + now.format(format)
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val dotPos = param.fileName().lastIndexOf(".")
        val fileExt = param.fileName().substring(dotPos + 1)
        val file = File("$path/$fileName.${fileExt.lowercase()}")
        val created = file.createNewFile()
        return if (created) {
            file.writeBytes(param.uploadedFile().readBytes())
            log.debug("file uploaded at ${file.absolutePath}")
            "$fileName.${fileExt.lowercase()}"
        } else {
            null
        }
    }

}