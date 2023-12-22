package org.sbas.services

import org.jboss.logging.Logger
import org.sbas.repositories.*
import java.time.Instant
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class DashboardService {

    @Inject
    lateinit var log: Logger

    @Inject
    private lateinit var bdasReqRepository: BdasReqRepository

    @Inject
    private lateinit var bdasTrnsRepository: BdasTrnsRepository

    @Inject
    private lateinit var bdasAprvRepository: BdasAprvRepository

    @Inject
    private lateinit var bdasAdmsRepository: BdasAdmsRepository

    @Transactional
    fun count(): MutableMap<String, Long> {
        val bdasReqCount = bdasReqRepository.count("rgst_dttm between ?1 and ?2", Instant.now().minusSeconds(60 * 60 * 24 * 30), Instant.now())
        val bdasTrnCount = bdasTrnsRepository.count("rgst_dttm between ?1 and ?2", Instant.now().minusSeconds(60 * 60 * 24 * 30), Instant.now())
        val bdasAprvCount = bdasAprvRepository.count("rgst_dttm between ?1 and ?2", Instant.now().minusSeconds(60 * 60 * 24 * 30), Instant.now())
        val bdasAdmCount = bdasAdmsRepository.count("rgst_dttm between ?1 and ?2", Instant.now().minusSeconds(60 * 60 * 24 * 30), Instant.now())

        val res: MutableMap<String, Long> = mutableMapOf()
        res["bdasReqCount"] = bdasReqCount
        res["bdasTrnCount"] = bdasTrnCount
        res["bdasAprvCount"] = bdasAprvCount
        res["bdasAdmCount"] = bdasAdmCount

        return res
    }
}