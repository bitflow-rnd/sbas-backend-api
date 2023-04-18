package org.sbas.schedulers

import io.quarkus.scheduler.Scheduled
import org.jboss.logging.Logger
import org.sbas.services.EgenService
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * 시간 주기적인 반복작업 수행
 */
@ApplicationScoped
class SbasScheduler {

    @Inject
    lateinit var log: Logger


    @Inject
    lateinit var egenService: EgenService

    /**
     * 매 12시간 마다 트리거 됨
     */
    @Scheduled(every = "12h")
    fun getHospitalInfos() {
        log.info("scheduler has started job")
        val res = egenService.syncHospitalInfos();
        log.info("scheduler has finished job, success = $res")
    }

}