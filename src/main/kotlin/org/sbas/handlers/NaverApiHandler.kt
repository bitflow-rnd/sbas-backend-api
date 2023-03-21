package org.sbas.handlers

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.restclients.NaverOcrRestClient
import org.sbas.restclients.NaverSensRestClient
import org.sbas.restparameters.NaverClovaOcrApiParams
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
class NaverApiHandler {

    @Inject
    lateinit var log: Logger

    @RestClient
    lateinit var naverOcrClient: NaverOcrRestClient

    fun clovaOcrRecognize(param: NaverClovaOcrApiParams) {
        naverOcrClient.recognize(param)
    }

}