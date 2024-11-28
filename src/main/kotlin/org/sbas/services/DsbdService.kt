package org.sbas.services

import jakarta.enterprise.context.ApplicationScoped
import org.jboss.logging.Logger
import org.sbas.constants.enums.BedStatCd
import org.sbas.dtos.DsbdCardDetail
import org.sbas.repositories.*
import java.time.Instant

@ApplicationScoped
class DsbdService(
  private val log: Logger,
  private val bdasReqRepository: BdasReqRepository,
  private val bdasReqAprvRepository: BdasReqAprvRepository,
  private val bdasAprvRepository: BdasAprvRepository,
  private val bdasTrnsRepository: BdasTrnsRepository,
  private val bdasAdmsRepository: BdasAdmsRepository,
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
  fun inst(period: Long): List<DsbdCardDetail> {
    val now = Instant.now()
    val time = now.minusSeconds(60 * 60 * 24 * period)
    val beforeTime = time.minusSeconds(60 * 60 * 24 * period)
    val bdasAdmsList = bdasAdmsRepository.findBdasAdmsInPeriod(time, now)
    val beforeBdasAdmsList = bdasAdmsRepository.findBdasAdmsInPeriod(beforeTime, time)
    val dsbdCardDetails = bdasAdmsList.groupBy { it.hospId }
      .map { (hospId, list) ->
        DsbdCardDetail(
          title = hospId,
          value = list.size.toLong(),
          beforeValue = beforeBdasAdmsList.filter { it.hospId == hospId }.size.toLong()
        )
      }
    return dsbdCardDetails
  }

  // staff



  // 중증도 현황
  fun getSvrtData(period: Long): List<DsbdCardDetail> {
    val now = Instant.now()
    val time = now.minusSeconds(60 * 60 * 24 * period)
    val beforeTime = time.minusSeconds(60 * 60 * 24 * period)
    val bdasReqList = bdasReqRepository.findBdasReqInPeriod(time, now)
    val beforeBdasReqList = bdasReqRepository.findBdasReqInPeriod(beforeTime, time)
    val dsbdCardDetails = bdasReqList.groupBy { it.svrtTypeCd }
      .map { (svrtTypeCd, list) ->
        DsbdCardDetail(
          title = svrtTypeCd!!,
          value = list.size.toLong(),
          beforeValue = beforeBdasReqList.filter { it.svrtTypeCd == svrtTypeCd }.size.toLong()
        )
      }
    return dsbdCardDetails
  }
}