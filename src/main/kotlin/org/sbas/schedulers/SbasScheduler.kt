package org.sbas.schedulers

import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.Scheduled.Schedules
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.jboss.logging.Logger
import org.sbas.restparameters.EgenApiEmrrmRltmUsefulSckbdInfoParams
import org.sbas.services.EgenService

/**
 * 시간 주기적인 반복작업 수행
 */
@ApplicationScoped
class SbasScheduler {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var egenService: EgenService

    private val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.Default)

    /**
     * 매 12시간 마다 트리거 됨
     */
    @Scheduled(every = "12h")
    fun getHospitalInfos() {
        log.info("scheduler has started job")
        val res = egenService.syncHospitalInfos();
        log.info("scheduler has finished job, success = $res")
    }

    @Schedules(
        value = [
            Scheduled(cron = "0 10 10 1/1 * ? *"),
            Scheduled(cron = "0 10 14 1/1 * ? *"),
            Scheduled(cron = "0 10 17 1/1 * ? *"),
        ]
    )
    fun updateHospitalBedInfos() {
        val param = EgenApiEmrrmRltmUsefulSckbdInfoParams(
            stage1 = null,
            stage2 = null,
            pageNo = null,
            numOfRows = "100",
        )
        log.info("scheduler has started job")
        egenService.saveUsefulSckbdInfo(param);
        log.info("scheduler has finished job, success")
    }

//    @Scheduled(every = "24h")
//    fun saveHsptlInfos() {
//        val res = egenService.saveHospitalByScheduler()
//    }

}