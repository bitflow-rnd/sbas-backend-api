package org.sbas.services

import jakarta.enterprise.context.ApplicationScoped
import org.jboss.logging.Logger
import org.sbas.dtos.DsbdCardDetail
import org.sbas.repositories.BdasReqRepository
import java.time.Instant

@ApplicationScoped
class DsbdService(
  private val log: Logger,
  private val bdasReqRepository: BdasReqRepository,
) {

  /**
   * 병상 배정 상태 현황 데이터 조회
   */
  fun getBedStatData(period: Long): List<DsbdCardDetail> {
    val now = Instant.now()
    val time = now.minusSeconds(60 * 60 * 24 * period)
    val beforeTime = time.minusSeconds(60 * 60 * 24 * period)

    val bdasReqList = bdasReqRepository.findBdasReqInPeriod(time, now)
    val beforeBdasReqList = bdasReqRepository.findBdasReqInPeriod(beforeTime, time)
    val dsbdCardDetails = bdasReqList.groupBy { it.bedStatCd }
      .map { (bedStatCd, list) ->
        DsbdCardDetail(
          title = bedStatCd,
          value = list.size.toLong(),
          beforeValue = beforeBdasReqList.filter { it.bedStatCd == bedStatCd }.size.toLong()
        )
      }
    log.debug("dsbdCardDetails: $dsbdCardDetails")
    return dsbdCardDetails
  }

  // 기관

  // staff

  // 중증도 현황

}