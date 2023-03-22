package org.sbas.handlers

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
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
    lateinit var UPLOAD_DIR_PUBLIC: String

    @ConfigProperty(name = "upload.private.dir")
    lateinit var UPLOAD_DIR_PRIVATE: String

    /**
     * 전체 공개 권한 파일 업로드
     */
    fun createPublicFile(param: FileUpload): Array<String>? {
        val now = LocalDateTime.now()
        val format = DateTimeFormatter.ofPattern("yyyyMM")
        val fileName = Instant.now().toEpochMilli()
        val dirName = now.format(format)
        val path = "$UPLOAD_DIR_PUBLIC/$dirName"
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
            arrayOf("$dirName/$fileName.${fileExt.lowercase()}", path, dirName, fileExt)
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
        val path = "$UPLOAD_DIR_PRIVATE/$dirName"
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

    fun moveFilePublicToPrivate(uri: String): Boolean {
        val dotPos = uri.lastIndexOf("/")
        val subpath = uri.substring(0, dotPos)
        val filename = uri.substring(dotPos + 1)
        val path = "$UPLOAD_DIR_PRIVATE/$subpath"
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        try {
            Files.move(
                File("$UPLOAD_DIR_PUBLIC/$uri").toPath(),
                File("$UPLOAD_DIR_PRIVATE/$uri").toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
            log.debug("file moved to $UPLOAD_DIR_PRIVATE/$uri")
            return true
        } catch (e: Exception) {
            log.error("exception when moving file")
        }
        return false
    }

}