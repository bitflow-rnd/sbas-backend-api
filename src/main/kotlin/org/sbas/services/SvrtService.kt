package org.sbas.services

import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.dtos.CovSfRsps
import org.sbas.dtos.SvrtInfoRsps
import org.sbas.dtos.SvrtPtSearchDto
import org.sbas.dtos.info.SvrtPtSearchParam
import org.sbas.entities.svrt.*
import org.sbas.handlers.NubisonAiSeverityAnalysisHandler
import org.sbas.repositories.SvrtAnlyRepository
import org.sbas.repositories.SvrtCollRepository
import org.sbas.repositories.SvrtCollSampleRepository
import org.sbas.repositories.SvrtPtRepository
import org.sbas.responses.CommonListResponse
import org.sbas.responses.CommonResponse
import org.sbas.restclients.DgmcHisRestClient
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
  private val svrtCollSampleRepository: SvrtCollSampleRepository,
  private val svrtAnlyHandler: NubisonAiSeverityAnalysisHandler,
  @RestClient private val fatimaHisRestClient: FatimaHisRestClient,
  @RestClient private val knuhHisRestClient: KnuhHisRestClient,
  @RestClient private val dgmcHisRestClient: DgmcHisRestClient,
) {
  fun findAllSvrtPt(): List<SvrtPt> {
    return svrtPtRepository.findAllWithMaxRgstSeq()
  }

  fun getLastSvrtAnly(ptId: String, rgstSeq: Int): CommonResponse<*> {
    val latestSvrtPt = svrtPtRepository.findByPtIdAndRgstSeq(ptId, rgstSeq)
      ?: return CommonResponse(null)
    val latestSvrtColl = svrtCollRepository.findByPtIdAndHospId(latestSvrtPt.id.ptId, latestSvrtPt.id.hospId)

    val svrtAnlyList = svrtAnlyRepository.findLastByPtId(
      ptId = ptId,
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
        }?.oxygenApply ?: "-" else "-",
      )
      svrtInfoRsps
    }

    return CommonResponse(rsps)
  }

  @Transactional
  fun saveWithMonitoringEnd(svrtPt: SvrtPt) {
    if (svrtPt.id.rgstSeq > 1) {
      val lastSvrtPt = svrtPtRepository.findByPtIdAndRgstSeq(svrtPt.id.ptId, svrtPt.id.rgstSeq - 1)
      lastSvrtPt?.endMonitoring(svrtPt.monStrtDt, svrtPt.monStrtTm)
    }

    svrtPtRepository.persist(svrtPt)
  }

  fun findSeverityInfos(ptId: String, rgstSeq: Int): CommonResponse<List<SvrtColl>> {
    val svrtCollList = svrtCollRepository.findAllByPtIdOrderByCollSeqAsc(ptId)
    return CommonResponse(svrtCollList)
  }

  @Transactional
  fun saveMntrInfoWithSample(ptId: String, pid: String) {
    val svrtPt = svrtPtRepository.findByPtId(ptId).maxByOrNull { it.id.rgstSeq }
      ?: return

    // 관찰 종료일이 없는 경우에만 수집
    if (svrtPt.monEndDt == null) {
      val svrtColls = svrtCollRepository.findByPtId(ptId).sortedBy { it.id.collSeq }
      val basedd = svrtColls.lastOrNull()?.rsltDt?.plusDays(1) ?: svrtPt.monStrtDt

      val sampleData = HisRestClientRequest(pid, basedd)
      var hisApiResponse: HisApiResponse? = null
      when {
        pid.startsWith("001") -> hisApiResponse = knuhHisRestClient.getKnuchSvrtMntrInfo(sampleData)
        pid.startsWith("002") -> hisApiResponse = knuhHisRestClient.getKnuhSvrtMntrInfo(sampleData)
        pid.startsWith("003") -> hisApiResponse = fatimaHisRestClient.getFatimaSvrtMntrInfo(sampleData)
        pid.startsWith("004") -> hisApiResponse = dgmcHisRestClient.getDgmcSvrtMntrInfo(sampleData)
      }
      log.debug("hisApiResponse: $hisApiResponse")

      val body = hisApiResponse?.body ?: return
      val svrtMntrInfo = body.last()

      // 마지막 데이터의 rsltDt가 basedd보다 이전이면 endMonitoring
      // 데이터가 20241008까지 있으면 현재 basedd는 20241009 이므로 20241008까지만 수집
      if (svrtMntrInfo.rsltDt < basedd) {
        svrtPt.endMonitoring(svrtMntrInfo.rsltDt, svrtMntrInfo.rsltTm)
        return
      }

      val svrtColl = svrtMntrInfo.toSvrtColl(
        ptId = svrtPt.id.ptId,
        hospId = svrtPt.id.hospId,
        rgstSeq = svrtPt.id.rgstSeq,
        collSeq = svrtColls.lastOrNull()?.id?.collSeq?.plus(1) ?: 1,
        pid = pid,
      )
      svrtCollRepository.persist(svrtColl)
    }
  }

  @Transactional
  fun saveInitMntrInfo(ptId: String, pid: String) {
    val svrtPt = svrtPtRepository.findByPtId(ptId).maxByOrNull { it.id.rgstSeq }
      ?: return

    var basedd = svrtPt.monStrtDt
    val today = StringUtils.getYyyyMmDd()

    val knuchSampleList = listOf("0010001", "0010002", "0010003", "0010004", "0010005", "0010006")
    val knuhSampleList = listOf("0020001", "0020002", "0020003", "0020004", "0020005", "0020006")
    val fatimaSampleList = listOf("0030001", "0030002", "0030003", "0030004", "0030005", "0030006")
    val dgmcSampleList = listOf("0040001", "0040002", "0040003", "0040004", "0040005", "0040006")
    val sampleList = knuchSampleList + knuhSampleList + fatimaSampleList + dgmcSampleList
    if (!sampleList.contains(svrtPt.pid)) {
      return
    }

    if (svrtPt.id.rgstSeq > 1) {
      val svrtColls = svrtCollRepository.findByPtId(ptId).sortedBy { it.id.collSeq }

      // 샘플 데이터 생성 및 요청
      val sampleData = HisRestClientRequest(pid, basedd)
      var hisApiResponse: HisApiResponse? = null
      when {
        pid.startsWith("001") -> hisApiResponse = knuhHisRestClient.getKnuchSvrtMntrInfo(sampleData)
        pid.startsWith("002") -> hisApiResponse = knuhHisRestClient.getKnuhSvrtMntrInfo(sampleData)
        pid.startsWith("003") -> hisApiResponse = fatimaHisRestClient.getFatimaSvrtMntrInfo(sampleData)
        pid.startsWith("004") -> hisApiResponse = dgmcHisRestClient.getDgmcSvrtMntrInfo(sampleData)
      }

      log.debug("hisApiResponse: $hisApiResponse")

      // 응답 데이터를 바탕으로 새로운 svrtColl 객체 생성
      val body = hisApiResponse?.body ?: return
      val svrtMntrInfo = body.last()
      val lastSvrtColl = svrtColls.last()

      val svrtColl = svrtMntrInfo.toSvrtColl(
        ptId = svrtPt.id.ptId,
        hospId = svrtPt.id.hospId,
        rgstSeq = svrtPt.id.rgstSeq,
        collSeq = lastSvrtColl.id.collSeq,
        pid = pid,
      )

      // 전원요청이면 마지막 데이터 삭제 후 새로 저장
      svrtCollRepository.delete(lastSvrtColl)
      svrtCollRepository.persist(svrtColl)
      return
    }

    // basedd가 오늘보다 이전이고 monEndDt가 null인 동안 반복
    while (basedd <= today && svrtPt.monEndDt == null) {
      // 수집 데이터를 정렬하여 가장 최신의 날짜를 기반으로 다음 시작 날짜 설정
      val svrtColls = svrtCollRepository.findByPtId(ptId).sortedBy { it.id.collSeq }
      basedd = svrtColls.lastOrNull()?.rsltDt?.plusDays(1) ?: svrtPt.monStrtDt

      // 샘플 데이터 생성 및 요청
      val sampleData = HisRestClientRequest(pid, basedd)
      var hisApiResponse: HisApiResponse? = null
      when {
        pid.startsWith("001") -> hisApiResponse = knuhHisRestClient.getKnuchSvrtMntrInfo(sampleData)
        pid.startsWith("002") -> hisApiResponse = knuhHisRestClient.getKnuhSvrtMntrInfo(sampleData)
        pid.startsWith("003") -> hisApiResponse = fatimaHisRestClient.getFatimaSvrtMntrInfo(sampleData)
        pid.startsWith("004") -> hisApiResponse = dgmcHisRestClient.getDgmcSvrtMntrInfo(sampleData)
      }

      log.debug("hisApiResponse: $hisApiResponse")

      // 응답 데이터를 바탕으로 새로운 svrtColl 객체 생성
      val body = hisApiResponse?.body ?: return
      val svrtMntrInfo = body.last()

      // 마지막 데이터의 rsltDt가 basedd보다 이전이면 endMonitoring
      // 데이터가 20241008까지 있으면 현재 basedd는 20241009 이므로 20241008까지만 수집
      if (svrtMntrInfo.rsltDt < basedd) {
        svrtPt.endMonitoring(svrtMntrInfo.rsltDt, svrtMntrInfo.rsltTm)
        return
      }

      val svrtColl = svrtMntrInfo.toSvrtColl(
        ptId = svrtPt.id.ptId,
        hospId = svrtPt.id.hospId,
        rgstSeq = svrtPt.id.rgstSeq,
        collSeq = svrtColls.lastOrNull()?.id?.collSeq?.plus(1) ?: 1,
        pid = pid,
      )
      svrtCollRepository.persist(svrtColl)
    }
  }

  @Transactional
  fun saveSvrtAnly(ptId: String, pid: String) {
    val svrtCollList = svrtCollRepository.findByPtIdOrderByRsltDtAsc(ptId) // 3
    if (svrtCollList.isEmpty()) return
    if (svrtCollList.last().rsltDt < StringUtils.getYyyyMmDd()) return

    val covSfList = svrtAnlyHandler.analyse(svrtCollList) // 주어진 날짜 + 3일까지의 예측값(+1, +2, +3)
    val findSvrtAnly = svrtAnlyRepository.findMaxAnlySeqByPtId(ptId)

    covSfList.forEachIndexed { idx, covSf -> // 0 1 2 3 4 5
      saveSvrtAnly(svrtCollList, idx, findSvrtAnly, pid, covSf)
    }
  }

  @Transactional
  fun saveInitSvrtAnly(ptId: String, pid: String) {
    val svrtCollList = svrtCollRepository.findByPtIdOrderByRsltDtAsc(ptId) // 3
    if (svrtCollList.isEmpty()) return

    val covSfList = svrtAnlyHandler.analyse(svrtCollList) // 주어진 날짜 + 3일까지의 예측값(+1, +2, +3)
    val findSvrtAnly = svrtAnlyRepository.findMaxAnlySeqByPtId(ptId)

    covSfList.forEachIndexed { idx, covSf -> // 0 1 2 3 4 5
      saveSvrtAnly(svrtCollList, idx, findSvrtAnly, pid, covSf)
    }
  }

  @Transactional
  fun findSvrtPtList(param: SvrtPtSearchParam): CommonListResponse<SvrtPtSearchDto> {
    log.debug("param: $param")

    // svrtPtList 조회
    val svrtPtList = svrtPtRepository.findSvrtPtList(param)

    svrtPtList.forEach {
      val findCovSF = findCovSF(it.ptId!!, it.hospId!!, it.rgstSeq)
      it.covSf = findCovSF
      it.updtDttm = findCovSF?.updtDttm ?: it.updtDttm
    }

    // covSf의 today, plusOneDay, plusTwoDay, plusThreeDay 값을 기준으로 정렬
    val sortedSvrtPtList = svrtPtList.sortedWith(
      compareByDescending<SvrtPtSearchDto> { it.covSf?.today }
        .thenByDescending { it.covSf?.plusOneDay }
        .thenByDescending { it.covSf?.plusTwoDay }
        .thenByDescending { it.covSf?.plusThreeDay }
    )

    val page = param.page ?: 1
    val size = 15  // 한 페이지에 15개씩

    // 페이징 처리: page와 size를 이용해 리스트 자르기
    val fromIndex = (page - 1) * size
    val toIndex = minOf(fromIndex + size, sortedSvrtPtList.size)

    // 유효한 범위인지 확인 후 subList로 자르기
    val pagedSvrtPtList = if (fromIndex in 0 until sortedSvrtPtList.size) {
      sortedSvrtPtList.subList(fromIndex, toIndex)
    } else {
      emptyList()
    }

    val count = svrtPtList.size

    return CommonListResponse(pagedSvrtPtList, count)
  }

  fun findCovSF(ptId: String, hospId: String, rgstSeq: Int): CovSfRsps? {
    val svrtAnlyList = svrtAnlyRepository.findLastByPtId(
      ptId = ptId,
    )
    if (svrtAnlyList.isEmpty()) return null
    // svrtAnlyList의 마지막 4개 항목을 가져옴
    val lastFourItems = svrtAnlyList.takeLast(4)

    return CovSfRsps(
      today = lastFourItems[0].covSf,
      plusOneDay = lastFourItems[1].covSf,
      plusTwoDay = lastFourItems[2].covSf,
      plusThreeDay = lastFourItems[3].covSf,
      updtDttm = lastFourItems[0].updtDttm,
    )
  }

  private fun saveSvrtAnly(
    svrtCollList: List<SvrtColl>,
    idx: Int,
    findSvrtAnly: SvrtAnly?,
    pid: String,
    covSf: Float
  ) {
    val rgstSeq = svrtCollList.last().id.rgstSeq
    val svrtColl = svrtCollList.getOrElse(idx) { svrtCollList.last() }
    val svrtAnlyId = SvrtAnlyId(
      ptId = svrtColl.id.ptId,
      hospId = svrtColl.id.hospId,
      rgstSeq = rgstSeq,
      msreDt = svrtColl.id.msreDt,
      // covSfList 의 size는 svrtCollList.size 보다 3개 많음.
      // -> svrtCollList.size 만큼은 svrtColl의 collSeq 사용, 나머지(예측값들)는 idx + 1
      collSeq = if (idx < svrtCollList.size) svrtColl.id.collSeq else idx + 1,
      anlyDt = StringUtils.getYyyyMmDd(),
      anlySeq = findSvrtAnly?.id?.anlySeq?.plus(1) ?: 1,
    )
    log.debug("svrtAnlyId: $svrtAnlyId")
    val svrtAnly = SvrtAnly(
      id = svrtAnlyId,
      pid = pid,
      collDt = StringUtils.convertInstantToYyyyMMdd(svrtColl.rgstDttm),
      collTm = StringUtils.convertInstantToHhmmss(svrtColl.rgstDttm),
      anlyTm = StringUtils.getHhMmSs(),
      covSf = covSf.toString(),
      prdtDt = if (idx < svrtCollList.size) null
        else svrtColl.id.msreDt.plusDays(idx - svrtCollList.size + 1),
    )
    svrtAnlyRepository.persist(svrtAnly)
  }

  @Transactional
  fun saveSvrtCollWithSample(ptId: String, pid: String) {
    val today = StringUtils.getYyyyMmDd()
    val svrtPt = svrtPtRepository.findByPtId(ptId).maxByOrNull { it.id.rgstSeq }

    val knuchSampleList = listOf("0010001", "0010002", "0010003", "0010004", "0010005", "0010006")
    val knuhSampleList = listOf("0020001", "0020002", "0020003", "0020004", "0020005", "0020006")
    val fatimaSampleList = listOf("0030001", "0030002", "0030003", "0030004", "0030005", "0030006")
    val dgmcSampleList = listOf("0040001", "0040002", "0040003", "0040004", "0040005", "0040006")
    val sampleList = knuchSampleList + knuhSampleList + fatimaSampleList + dgmcSampleList
    if (!sampleList.contains(pid)) {
      return
    }

    if (svrtPt != null && svrtPt.id.rgstSeq > 1) {
      val svrtCollSample = svrtCollSampleRepository.findByPidAndDate(pid, today)
        ?: return
      val svrtColls = svrtCollRepository.findByPtId(ptId).sortedBy { it.id.collSeq }
      val lastSvrtColl = svrtColls.last()

      // 전원요청이면 마지막 데이터 삭제 후 새로 저장
      svrtCollRepository.delete(lastSvrtColl)
      val toSvrtColl = svrtCollSample.toSvrtColl(ptId, svrtPt.id.rgstSeq, lastSvrtColl.id.collSeq)
      svrtCollRepository.persist(toSvrtColl)
      return
    }

    val svrtCollSampleList = svrtCollSampleRepository.findByPidBeforeDate(pid, today)
    svrtCollSampleList.forEachIndexed { idx, sample ->
      val toSvrtColl = sample.toSvrtColl(ptId, 1, idx + 1)
      svrtCollRepository.persist(toSvrtColl)
    }

  }
}