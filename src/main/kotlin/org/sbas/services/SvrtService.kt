package org.sbas.services

import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.dtos.SvrtInfoRsps
import org.sbas.entities.svrt.SvrtAnly
import org.sbas.entities.svrt.SvrtAnlyId
import org.sbas.entities.svrt.SvrtColl
import org.sbas.entities.svrt.SvrtPt
import org.sbas.handlers.NubisonAiSeverityAnalysisHandler
import org.sbas.repositories.SvrtAnlyRepository
import org.sbas.repositories.SvrtCollRepository
import org.sbas.repositories.SvrtPtRepository
import org.sbas.responses.CommonResponse
import org.sbas.restclients.FatimaHisRestClient
import org.sbas.restclients.HisRestClientRequest
import org.sbas.restclients.KnuhHisRestClient
import org.sbas.restdtos.response.HisApiResponse
import org.sbas.utils.StringUtils
import org.sbas.utils.plusDays

@ApplicationScoped
class SvrtService(
  private val log: Logger,
  private val svrtAnlyRepository: SvrtAnlyRepository,
  private val svrtCollRepository: SvrtCollRepository,
  private val svrtPtRepository: SvrtPtRepository,
  private val svrtAnlyHandler: NubisonAiSeverityAnalysisHandler,
  @RestClient private val fatimaHisRestClient: FatimaHisRestClient,
  @RestClient private val knuhHisRestClient: KnuhHisRestClient,
) {
  fun findAllSvrtPt(): List<SvrtPt> {
    val all = svrtPtRepository.findAllWithMaxRgstSeq()
    return all
  }

  fun getLastSvrtAnlyByPtId(ptId: String): CommonResponse<*> {
    val svrtInfo = svrtAnlyRepository.getSvrtInfo(ptId)

    val latestSvrtColl = svrtCollRepository.findAllByPtIdOrderByCollSeqAsc(ptId).last()
    val svrtAnlyList = svrtAnlyRepository.findAllByPtIdAndHospIdAndCollSeq(
      ptId = ptId,
      hospId = latestSvrtColl.id.hospId,
      collSeq = latestSvrtColl.id.collSeq
    )
    val rsps = svrtAnlyList.map {
      val svrtInfoRsps = SvrtInfoRsps(
        ptId = it.id.ptId,
        hospId = it.id.hospId,
        anlyDt = it.id.anlyDt,
        msreDt = it.id.msreDt,
        prdtDt = it.prdtDt,
        covSf = it.covSf.toString(),
        oxygenApply = if (it.prdtDt == null) latestSvrtColl.oxygenApply!! else "-",
      )
      svrtInfoRsps
    }

    return CommonResponse(svrtInfo)
  }

  fun findSeverityInfos(ptId: String): CommonResponse<List<SvrtColl>> {
    return CommonResponse(svrtCollRepository.findAllByPtIdOrderByCollSeqAsc(ptId))
  }

  @Transactional
  fun saveMntrInfoWithSample(pid: String): SvrtColl? {
    val svrtPt = svrtPtRepository.findByPid(pid) ?: return null

    // 관찰 종료일이 없는 경우에만 수집
    if (svrtPt.monEndDt == null) {
      val svrtColls = svrtCollRepository.findByPidAndHospId(pid, svrtPt.id.hospId)
      val basedd = svrtColls.lastOrNull()?.id?.msreDt?.plusDays(1) ?: svrtPt.monStrtDt

      val sampleData = HisRestClientRequest(pid, basedd)
      var hisApiResponse: HisApiResponse? = null
      if (pid.startsWith("001")) {
        hisApiResponse = knuhHisRestClient.getKnuchSvrtMntrInfo(sampleData)
      } else if (pid.startsWith("002")) {
        hisApiResponse = knuhHisRestClient.getKnuhSvrtMntrInfo(sampleData)
      } else if (pid.startsWith("003")) {
        hisApiResponse = fatimaHisRestClient.getFatimaSvrtMntrInfo(sampleData)
      }
      log.debug("hisApiResponse: $hisApiResponse")

      val body = hisApiResponse!!.body
      // 리스트가 비어있거나, 마지막 데이터의 msreDt가 basedd와 다르면 endMonitoring
      if (body.isEmpty() || body.last().msreDt != basedd) {
        svrtPt.endMonitoring(basedd, StringUtils.getHhMmSs())
        return null
      }

      val svrtMntrInfo = body.last()
      val svrtColl = svrtMntrInfo.toSvrtColl(
        ptId = svrtPt.id.ptId,
        hospId = svrtPt.id.hospId,
        rgstSeq = svrtPt.id.rgstSeq,
        collSeq = svrtColls.lastOrNull()?.id?.collSeq?.plus(1) ?: 1,
      )
      svrtCollRepository.persist(svrtColl)
      return svrtColl
    } else {
      return null
    }
  }

  @Transactional
  fun saveSvrtAnly(ptId: String, pid: String) {
    val svrtCollList = svrtCollRepository.findByPidOrderByRsltDtAsc(pid) // 3
    if (svrtCollList.isEmpty()) return
//    val filteredSvrtCollList = svrtCollList.filter { !it.isMntrInfoValueBlank() }
    val covSfList = svrtAnlyHandler.analyse(pid, svrtCollList) // 6
    val findSvrtAnly = svrtAnlyRepository.findByPtIdAndPidOrderByAnlySeqAsc(ptId, pid)

    covSfList.forEachIndexed { idx, covSf -> // 0 1 2 3 4 5
      val svrtColl = svrtCollList.getOrElse(idx) { svrtCollList.last() }
      val svrtAnlyId = SvrtAnlyId(
        ptId = svrtColl.id.ptId,
        hospId = svrtColl.id.hospId,
        rgstSeq = svrtColl.id.rgstSeq,
        msreDt = svrtColl.id.msreDt,
        collSeq = if (idx < svrtCollList.size) svrtColl.id.collSeq else idx + 1,
        anlyDt = StringUtils.getYyyyMmDd(),
        anlySeq = findSvrtAnly?.id?.anlySeq?.plus(1) ?: 1,
      )
      val svrtAnly = SvrtAnly(
        id = svrtAnlyId,
        pid = pid,
        collDt = StringUtils.convertInstantToYyyyMMdd(svrtColl.rgstDttm),
        collTm = StringUtils.convertInstantToHhmmss(svrtColl.rgstDttm),
        anlyTm = StringUtils.getHhMmSs(),
        covSf = covSf.toString(),
        prdtDt = if (idx >= svrtCollList.size) StringUtils.getYyyyMmDd()
          .plusDays(idx - svrtCollList.size + 1) else null,
      )
      svrtAnlyRepository.persist(svrtAnly)
    }
  }
}