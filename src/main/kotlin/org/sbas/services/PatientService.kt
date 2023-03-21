package org.sbas.services

import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.entities.info.InfoPt
import org.sbas.handlers.FileHandler
import org.sbas.repositories.InfoPtRepository
import org.sbas.response.StringResponse
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
        // Todo: Naver Clova OCR call

        // Todo: Then move from public to private
        if (fileuri != null) {
            handler1.moveFilePublicToPrivate(fileuri)
        }
    }

}