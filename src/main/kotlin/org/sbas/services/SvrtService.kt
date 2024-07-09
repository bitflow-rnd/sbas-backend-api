package org.sbas.services

import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.dtos.CovSfRsps
import org.sbas.dtos.SvrtInfoRsps
import org.sbas.dtos.SvrtPtSearchDto
import org.sbas.dtos.info.InfoPtSearchParam
import org.sbas.entities.svrt.SvrtAnly
import org.sbas.entities.svrt.SvrtAnlyId
import org.sbas.entities.svrt.SvrtColl
import org.sbas.entities.svrt.SvrtPt
import org.sbas.handlers.NubisonAiSeverityAnalysisHandler
import org.sbas.repositories.SvrtAnlyRepository
import org.sbas.repositories.SvrtCollRepository
import org.sbas.repositories.SvrtPtRepository
import org.sbas.responses.CommonListResponse
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
    return svrtPtRepository.findAllWithMaxRgstSeq()
  }

  fun getLastSvrtAnly(ptId: String): CommonResponse<*> {
    val latestSvrtPt = svrtPtRepository.findByPtId(ptId).maxByOrNull { it.id.rgstSeq }
      ?: return CommonResponse(null)
    val latestSvrtColl = svrtCollRepository.findByPtIdAndHospId(latestSvrtPt.id.ptId, latestSvrtPt.id.hospId)

    val svrtAnlyList = svrtAnlyRepository.findLastByPtIdAndHospId(
      ptId = ptId,
      hospId = latestSvrtPt.id.hospId,
    )
    val rsps = svrtAnlyList.map { svrtAnly ->
      val svrtInfoRsps = SvrtInfoRsps(
        ptId = svrtAnly.id.ptId,
        hospId = svrtAnly.id.hospId,
        anlyDt = svrtAnly.id.anlyDt,
        msreDt = svrtAnly.id.msreDt,
        prdtDt = svrtAnly.prdtDt,
        covSf = svrtAnly.covSf.toString(),
        oxygenApply = if (svrtAnly.prdtDt == null) latestSvrtColl.find { svrtColl ->
          svrtColl.id.collSeq == svrtAnly.id.collSeq
        }?.oxygenApply!! else "-",
      )
      svrtInfoRsps
    }

    return CommonResponse(rsps)
  }

  fun findSeverityInfos(ptId: String): CommonResponse<List<SvrtColl>> {
    return CommonResponse(svrtCollRepository.findAllByPtIdOrderByCollSeqAsc(ptId))
  }

  @Transactional
  fun saveMntrInfoWithSample(pid: String): SvrtColl? {
    val svrtPt = svrtPtRepository.findByPid(pid) ?: return null

    // 관찰 종료일이 없는 경우에만 수집
    if (svrtPt.monEndDt == null) {
      val svrtColls = svrtCollRepository.findByPidAndHospId(pid, svrtPt.id.hospId).sortedBy { it.id.collSeq }
      val basedd = svrtColls.lastOrNull()?.rsltDt?.plusDays(1) ?: svrtPt.monStrtDt

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
      if (body.isEmpty() || body.last().rsltDt != basedd) {
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
    val svrtCollList = svrtCollRepository.findByPtIdAndPidOrderByRsltDtAsc(ptId, pid) // 3
    if (svrtCollList.isEmpty()) return
    if (svrtCollList.last().rsltDt < StringUtils.getYyyyMmDd()) return

    val covSfList = svrtAnlyHandler.analyse(svrtCollList) // 주어진 날짜 + 3일까지의 예측값(+1, +2, +3)
    val findSvrtAnly = svrtAnlyRepository.findMaxAnlySeqByPtIdAndPid(ptId, pid)

    covSfList.forEachIndexed { idx, covSf -> // 0 1 2 3 4 5
      val svrtColl = svrtCollList.getOrElse(idx) { svrtCollList.last() }
      val svrtAnlyId = SvrtAnlyId(
        ptId = svrtColl.id.ptId,
        hospId = svrtColl.id.hospId,
        rgstSeq = svrtColl.id.rgstSeq,
        msreDt = svrtColl.id.msreDt,
        // covSfList 의 size는 svrtCollList.size 보다 3개 많음.
        // -> svrtCollList.size 만큼은 svrtColl의 collSeq 사용, 나머지(예측값들)는 idx + 1
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

  @Transactional
  fun findSvrtPtList(param: InfoPtSearchParam): CommonListResponse<SvrtPtSearchDto> {
    val svrtPtList = svrtPtRepository.findSvrtPtList(param)
    svrtPtList.forEach {
      it.covSf = findCovSF(it.ptId!!)
    }
    val count = svrtPtRepository.countSvrtPtList(param)
    return CommonListResponse(svrtPtList, count.toInt())
  }

  fun findCovSF(ptId: String): CovSfRsps? {
    val latestSvrtPt = svrtPtRepository.findByPtId(ptId).maxByOrNull { it.id.rgstSeq }
      ?: return null
    val svrtAnlyList = svrtAnlyRepository.findLastByPtIdAndHospId(
      ptId = ptId,
      hospId = latestSvrtPt.id.hospId,
    )
    // svrtAnlyList의 마지막 4개 항목을 가져옴
    val lastFourItems = svrtAnlyList.takeLast(4)

    return CovSfRsps(
      today = lastFourItems[0].covSf,
      plusOneDay = lastFourItems[1].covSf,
      plusTwoDay = lastFourItems[2].covSf,
      plusThreeDay = lastFourItems[3].covSf,
    )
  }
}