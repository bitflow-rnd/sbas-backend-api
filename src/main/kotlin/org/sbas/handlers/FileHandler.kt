package org.sbas.handlers

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.dtos.FileDto
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import kotlin.io.path.readBytes


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class FileHandler {

    @Inject
    lateinit var log: Logger

    @ConfigProperty(name = "upload.public.dir")
    lateinit var UPLOAD_PATH_LOCAL_PUBLIC: String

    @ConfigProperty(name = "upload.private.dir")
    lateinit var UPLOAD_PATH_LOCAL_PRIVATE: String

    @ConfigProperty(name = "upload.path.middle")
    lateinit var UPLOAD_PATH_MIDDLE: String


    /**
     * 전체 공개 권한 파일 업로드
     */
    fun createPublicFile(param: FileUpload): FileDto? {
        val format = DateTimeFormatter.ofPattern("yyyyMM")
        val yyyyMm = LocalDateTime.now().format(format)
        val absolutLocalUplodPathStr = "$UPLOAD_PATH_LOCAL_PUBLIC/$yyyyMm"
        val absolutLocalUplodDir = File(absolutLocalUplodPathStr)
        if (!absolutLocalUplodDir.exists()) {
            absolutLocalUplodDir.mkdirs()
        }
        val dotPos = param.fileName().lastIndexOf(".")
        val fileExt = param.fileName().substring(dotPos + 1).lowercase()
        val fileNameWithExt = "${Instant.now().toEpochMilli()}.$fileExt"
        val file = File("$absolutLocalUplodPathStr/$fileNameWithExt")
        val created = file.createNewFile()
        val ret = FileDto(filename = fileNameWithExt, localPath = absolutLocalUplodPathStr, uriPath = "/$UPLOAD_PATH_MIDDLE/$yyyyMm")

        return if (created) {
            file.writeBytes(param.uploadedFile().readBytes())
            log.debug("file uploaded at ${file.absolutePath}")
            ret
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
        val dirName = now.format(format)
        val path = "$UPLOAD_PATH_LOCAL_PRIVATE/$dirName"
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
            "$dirName/$fileName.${fileExt.lowercase()}"
        } else {
            null
        }
    }

    fun moveFilePublicToPrivate(publcLocalPath: String, filename: String): Boolean {
        val privtLocalPath = publcLocalPath.replace("/public/", "/private/")
        val dir = File(privtLocalPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        try {
            Files.move(
                File("$publcLocalPath/$filename").toPath(),
                File("$privtLocalPath/$filename").toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
            log.debug("file moved to $privtLocalPath/$filename")
            return true
        } catch (e: Exception) {
            log.error("exception when moving file")
        }
        return false
    }


}