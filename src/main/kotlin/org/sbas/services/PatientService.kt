package org.sbas.services

import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.entities.info.InfoPt
import org.sbas.handlers.FileHandler
import org.sbas.handlers.NaverApiHandler
import org.sbas.repositories.InfoPtRepository
import org.sbas.response.StringResponse
import org.sbas.restparameters.NaverOcrApiParams
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional


/**
 * 환자정보를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class PatientService {

    @Inject
    lateinit var log: Logger

    @Inject
    private lateinit var infoPtRepository: InfoPtRepository

    @Inject
    private lateinit var handler1: FileHandler

    @Inject
    private lateinit var handler2: NaverApiHandler

    @Transactional
    fun saveInfoPt(infoPt: InfoPt): StringResponse {
        infoPt.rgstUserId = "jiseong"
        infoPt.updtUserId = "jiseong"

        infoPtRepository.persist(infoPt)

        return StringResponse(infoPt.id)
    }

    @Transactional
    fun uploadEpidReport(param: FileUpload) {
        val fileuri = handler1.createPublicFile(param)
        if (fileuri != null) {
            // Naver Clova OCR call
            val texts = handler2.recognizeImage(fileuri)
            log.debug("texts are $texts")
            // Then move from public to private
            handler1.moveFilePublicToPrivate(fileuri)
        }
    }

}