package org.sbas.services

import org.jboss.logging.Logger
import org.sbas.entities.info.InfoCrew
import org.sbas.repositories.InfoCrewRepository
import org.sbas.repositories.InfoHospRepository
import org.sbas.repositories.InfoInstRepository
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class OrganiztnService {

    @Inject
    private lateinit var log: Logger

    @Inject
    private lateinit var infoCrewRepository: InfoCrewRepository

    @Inject
    private lateinit var infoHospRepository: InfoHospRepository

    @Inject
    private lateinit var infoInstRepository: InfoInstRepository

    @Transactional
    fun findInfoHosp() {
        val infoHospList = infoHospRepository.findAll().list()
    }

    /**
     * 구급대 목록 조회
     * @param dstrCd1
     * @param dstrCd2
     */
    @Transactional
    fun findFireStatns(dstrCd1: String, dstrCd2: String): MutableMap<String, Any> {
        val res = mutableMapOf<String, Any>()
        val fireStatns = infoInstRepository.findFireStatns(dstrCd1, dstrCd2)
        val count = fireStatns.size
        res["fireStatns"] = fireStatns
        res["count"] = count
        return res
    }

    @Transactional
    fun findInfoCrews(instId: String): List<InfoCrew> {
        return infoCrewRepository.findInfoCrews(instId)
    }
}