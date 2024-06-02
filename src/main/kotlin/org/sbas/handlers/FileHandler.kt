package org.sbas.handlers

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.dtos.FileDto
import org.sbas.utils.CustomizedException
import org.sbas.utils.StringUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.Instant
import kotlin.io.path.readBytes

@ApplicationScoped
class FileHandler {

    @Inject
    private lateinit var log: Logger

    @ConfigProperty(name = "upload.public.dir")
    private lateinit var UPLOAD_PATH_LOCAL_PUBLIC: String

    @ConfigProperty(name = "upload.private.dir")
    private lateinit var UPLOAD_PATH_LOCAL_PRIVATE: String

    @ConfigProperty(name = "upload.path.middle")
    private lateinit var UPLOAD_PATH_MIDDLE: String

    /**
     * 전체 공개 권한 파일 업로드
     */
    fun createPublicFile(fileUpload: FileUpload): FileDto {
        val (fileName, fileExt) = getFilenameAndExt(fileUpload)
        val localPath = getLocalPath(UPLOAD_PATH_LOCAL_PUBLIC)
        val uriPath = "/public/$UPLOAD_PATH_MIDDLE/${StringUtils.getYyyyMM()}"

        val file = makeFileWithPath("$fileName.$fileExt", UPLOAD_PATH_LOCAL_PUBLIC)
        log.debug("file uploaded at ${file.absolutePath}")
        file.writeBytes(fileUpload.uploadedFile().readBytes())

        return FileDto(fileName = "$fileName.$fileExt", fileExt = fileExt, localPath = localPath, uriPath = uriPath)
    }

    /**
     * 사용자 공개 권한 파일 업로드
     */
    fun createPrivateFile(fileUpload: FileUpload): FileDto {
        val (fileName, fileExt) = getFilenameAndExt(fileUpload)
        val localPath = getLocalPath(UPLOAD_PATH_LOCAL_PRIVATE)
        val uriPath = "/private/$UPLOAD_PATH_MIDDLE/${StringUtils.getYyyyMM()}"

        val file = makeFileWithPath("$fileName.$fileExt", UPLOAD_PATH_LOCAL_PRIVATE)
        log.debug("file uploaded at ${file.absolutePath}")
        file.writeBytes(fileUpload.uploadedFile().readBytes())

        return FileDto(fileName = "$fileName.$fileExt", fileExt = fileExt, localPath = localPath, uriPath = uriPath)
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

    private fun makeFileWithPath(fileName: String, path: String): File {
        val localPath = getLocalPath(path)

        val file = File("$localPath/$fileName")
        val isCreated = file.createNewFile()

        return if (isCreated) {
            file
        } else {
            throw CustomizedException("파일 생성 시 에러 발생", Response.Status.INTERNAL_SERVER_ERROR)
        }
    }

    private fun getLocalPath(path: String): String {
        val dirName = StringUtils.getYyyyMM()
        val localPath = "$path/$dirName"
        val dir = File(localPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return localPath
    }

    private fun getFilenameAndExt(fileUpload: FileUpload): Pair<Long, String> {
        val fileName = Instant.now().toEpochMilli()

        val dotPos = fileUpload.fileName().lastIndexOf(".")
        val fileExt = fileUpload.fileName().substring(dotPos + 1).lowercase()

        return Pair(fileName, fileExt)
    }
}