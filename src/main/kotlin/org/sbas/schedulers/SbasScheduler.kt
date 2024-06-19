package org.sbas.schedulers

import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.Scheduled.Schedules
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.jboss.logging.Logger
import org.sbas.restdtos.EgenApiEmrrmRltmUsefulSckbdInfoParams
import org.sbas.services.EgenService
import org.sbas.services.SvrtService

/**
 * 시간 주기적인 반복작업 수행
 */
@ApplicationScoped
class SbasScheduler {

  @Inject
  private lateinit var log: Logger

  @Inject
  private lateinit var egenService: EgenService

  @Inject
  private lateinit var svrtService: SvrtService

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

  /**
   * 매일 오후 11시 50분에 실행
   */
  @Scheduled(cron = "0 50 23 * * ?")
  fun saveSvrtColl() {
    // 0030001, 20220103 ~ 20220112
    // 0030002, 20211221 ~ 20211225
    svrtService.saveFatimaMntrInfoWithSample("0030001")
    svrtService.saveFatimaMntrInfoWithSample("0030002")
  }

  //    @Scheduled(every = "24h")
  //    fun saveHsptlInfos() {
  //        val res = egenService.saveHospitalByScheduler()
  //    }

}