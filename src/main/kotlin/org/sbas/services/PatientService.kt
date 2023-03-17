package org.sbas.services

import org.jboss.logging.Logger
import org.sbas.entities.info.InfoPt
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

    @Transactional
    fun saveInfoPt(infoPt: InfoPt): StringResponse {
        infoPt.rgstUserId = "jiseong"
        infoPt.updtUserId = "jiseong"

        infoPtRepository.persist(infoPt)

        return StringResponse(infoPt.id)
    }
}