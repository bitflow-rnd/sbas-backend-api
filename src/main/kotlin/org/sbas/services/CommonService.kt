package org.sbas.services

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeId
import org.sbas.repositories.BaseCodeRepository
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import kotlin.io.path.readBytes


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class CommonService {

    @Inject
    lateinit var log: Logger

    @ConfigProperty(name = "upload.public.dir")
    lateinit var UPLOAD_DIR_PUBLIC: String

    @Inject
    private lateinit var baseCodeRepository: BaseCodeRepository

    @Transactional
    fun findBaseCode(): List<BaseCode> {
        return baseCodeRepository.findAll().list()
    }

    @Transactional
    fun delCodeGrps(baseCodeId: BaseCodeId) {
        val findBaseCode = baseCodeRepository.findById(baseCodeId)
        if (findBaseCode != null) {
            baseCodeRepository.delete(findBaseCode)
        }
    }

    @Transactional
    fun fileUpload(param1: String, param2: FileUpload) {
        val now = LocalDateTime.now()
        val format = DateTimeFormatter.ofPattern("yyyyMM")
        val fileName = Instant.now().toEpochMilli()
        val path = UPLOAD_DIR_PUBLIC + "/" + now.format(format)
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val dotPos = param2.fileName().lastIndexOf(".")
        val fileExt = param2.fileName().substring(dotPos + 1)
        val file = File("$path/$fileName.${fileExt.lowercase()}")
        val created = file.createNewFile()
        if (created) {
            file.writeBytes(param2.uploadedFile().readBytes())
            log.debug("file uploaded at ${file.absolutePath}")
        }
    }

}