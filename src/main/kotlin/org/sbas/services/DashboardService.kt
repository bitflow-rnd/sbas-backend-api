package org.sbas.services

import org.jboss.logging.Logger
import org.sbas.repositories.*
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional


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
    private lateinit var bdasTrnRepository: BdasTrnRepository

    @Inject
    private lateinit var bdasAprvRepository: BdasAprvRepository

    @Inject
    private lateinit var bdasAdmRepository: BdasAdmRepository

    @Transactional
    fun count(): MutableMap<String, Long> {
        val bdasReqCount = bdasReqRepository.count("rgst_dttm between ?1 and ?2", Instant.now().minusSeconds(60 * 60 * 24 * 30), Instant.now())
        val bdasTrnCount = bdasTrnRepository.count("rgst_dttm between ?1 and ?2", Instant.now().minusSeconds(60 * 60 * 24 * 30), Instant.now())
        val bdasAprvCount = bdasAprvRepository.count("rgst_dttm between ?1 and ?2", Instant.now().minusSeconds(60 * 60 * 24 * 30), Instant.now())
        val bdasAdmCount = bdasAdmRepository.count("rgst_dttm between ?1 and ?2", Instant.now().minusSeconds(60 * 60 * 24 * 30), Instant.now())

        val res: MutableMap<String, Long> = mutableMapOf()
        res["bdasReqCount"] = bdasReqCount
        res["bdasTrnCount"] = bdasTrnCount
        res["bdasAprvCount"] = bdasAprvCount
        res["bdasAdmCount"] = bdasAdmCount

        return res
    }
}