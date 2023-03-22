package org.sbas.services

import org.jboss.logging.Logger
import org.sbas.entities.info.InfoCrew
import org.sbas.entities.info.InfoCrewId
import org.sbas.repositories.InfoCrewRepository
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.NotFoundException


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class OrganiztnService {

    @Inject
    private lateinit var log: Logger

    @Inject
    private lateinit var infoCrewRepository: InfoCrewRepository

    @Transactional
    fun findInfoCrews(): MutableMap<String, Any> {
        val res = mutableMapOf<String, Any>()
        val infoCrews = infoCrewRepository.findAll().list()
        val count = infoCrews.size
        res["infoCrews"] = infoCrews
        res["count"] = count
        return res
    }

    @Transactional
    fun findInfoCrewById(instId: String, crewId: String): InfoCrew {
        val infoCrewId = InfoCrewId(instId = instId, crewId = crewId)
        return infoCrewRepository.findById(infoCrewId) ?: throw NotFoundException("crew not found")
    }
}