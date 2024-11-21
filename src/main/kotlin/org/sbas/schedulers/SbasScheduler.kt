package org.sbas.schedulers

import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.Scheduled.Schedules
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
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
   * 매일 23시 50분에 실행
   */
  @Scheduled(cron = "0 50 23 * * ?")
  fun saveSvrtColl() {
    val knuchSampleList = listOf("0010001", "0010002", "0010003", "0010004", "0010005")
    val knuhSampleList = listOf("0020001", "0020002", "0020003", "0020004", "0020005")
    val fatimaSampleList = listOf("0030001", "0030002", "0030003", "0030004", "0030005")
    val dgmcSampleList = listOf("0040001", "0040002", "0040003", "0040004", "0040005")
    val sampleList = knuchSampleList + knuhSampleList + fatimaSampleList + dgmcSampleList

    svrtService.findAllSvrtPt().forEach { svrtPt ->
      if (sampleList.contains(svrtPt.pid)) {
        svrtService.saveMntrInfoWithSample(svrtPt.id.ptId, svrtPt.pid)
      }
    }
  }

  /**
   * 매일 00시 30분에 실행
   */
  @Scheduled(cron = "0 30 0 * * ?")
  fun saveSvrtAnly() {
    val knuchSampleList = listOf("0010001", "0010002", "0010003", "0010004", "0010005")
    val knuhSampleList = listOf("0020001", "0020002", "0020003", "0020004", "0020005")
    val fatimaSampleList = listOf("0030001", "0030002", "0030003", "0030004", "0030005")
    val dgmcSampleList = listOf("0040001", "0040002", "0040003", "0040004", "0040005")
    val sampleList = knuchSampleList + knuhSampleList + fatimaSampleList + dgmcSampleList

    svrtService.findAllSvrtPt().forEach { svrtPt ->
      if (sampleList.contains(svrtPt.pid)) {
        svrtService.saveSvrtAnly(svrtPt.id.ptId, svrtPt.pid)
      }
    }
  }
}
