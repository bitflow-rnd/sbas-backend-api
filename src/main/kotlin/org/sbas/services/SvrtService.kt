package org.sbas.services

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.json.JSONObject
import org.sbas.dtos.SvrtInfoRsps
import org.sbas.entities.svrt.*
import org.sbas.handlers.NubisonAiSeverityAnalysisHandler
import org.sbas.repositories.SvrtAnlyRepository
import org.sbas.repositories.SvrtCollRepository
import org.sbas.repositories.SvrtPtRepository
import org.sbas.responses.CommonResponse
import org.sbas.restclients.FatimaHisRestClient
import org.sbas.restclients.HisRestClientRequest
import org.sbas.restclients.KnuhHisRestClient
import org.sbas.utils.StringUtils
import org.sbas.utils.StringUtils.Companion.getYyyyMmDdWithHyphen
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

  fun getSvrtAnlyById(id: SvrtAnlyId): SvrtAnly? {
    return svrtAnlyRepository.findById(id)
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

  @Transactional
  fun saveSvrtAnly(anly: SvrtAnly) {
    svrtAnlyRepository.persist(anly)
  }

  fun deleteSvrtAnlyById(id: SvrtAnlyId): Boolean {
    return svrtAnlyRepository.deleteById(id)
  }


  fun getSvrtCollById(id: SvrtCollId): SvrtColl? {
    return svrtCollRepository.findById(id)
  }

  fun getSvrtCollByPidAndMsreDt(pid: String): List<SvrtColl>? {
    return svrtCollRepository.findByPidOrderByMsreDtAsc(pid)
  }

  /**
   * Converts data from DB to request data format
   */
  fun getSvrtRequestData(svrtCollList: List<SvrtColl>): String {
    val requestMap = mapOf(
      "ALT" to mutableMapOf<String, Float>(),
      "AST" to mutableMapOf(),
      "BUN" to mutableMapOf(),
      "Creatinine" to mutableMapOf(),
      "Hemoglobin" to mutableMapOf(),
      "LDH" to mutableMapOf(),
      "Lymphocytes" to mutableMapOf(),
      "Neutrophils" to mutableMapOf(),
      "Platelet count" to mutableMapOf(),
      "Potassium" to mutableMapOf(),
      "Sodium" to mutableMapOf(),
      "WBC Count" to mutableMapOf(),
      "CRP" to mutableMapOf(),
      "BDTEMP" to mutableMapOf(),
      "BREATH" to mutableMapOf(),
      "DBP" to mutableMapOf(),
      "PULSE" to mutableMapOf(),
      "SBP" to mutableMapOf(),
      "SPO2" to mutableMapOf()
    )
    var msreDt: String
    svrtCollList.forEach {
      msreDt = getYyyyMmDdWithHyphen(it.id.msreDt)
      (requestMap["ALT"] as HashMap<String, Float>)[msreDt] = it.alt!!.toFloat()
      (requestMap["AST"] as HashMap<String, Float>)[msreDt] = it.ast!!.toFloat()
      (requestMap["BUN"] as HashMap<String, Float>)[msreDt] = it.bun!!.toFloat()
      (requestMap["Creatinine"] as HashMap<String, Float>)[msreDt] = it.cre!!.toFloat()
      (requestMap["Hemoglobin"] as HashMap<String, Float>)[msreDt] = it.hem!!.toFloat()
      (requestMap["LDH"] as HashMap<String, Float>)[msreDt] = it.ldh!!.toFloat()
      (requestMap["Lymphocytes"] as HashMap<String, Float>)[msreDt] = it.lym!!.toFloat()
      (requestMap["Neutrophils"] as HashMap<String, Float>)[msreDt] = it.neu!!.toFloat()
      (requestMap["Platelet count"] as HashMap<String, Float>)[msreDt] = it.pla!!.toFloat()
      (requestMap["Potassium"] as HashMap<String, Float>)[msreDt] = it.pot!!.toFloat()
      (requestMap["Sodium"] as HashMap<String, Float>)[msreDt] = it.sod!!.toFloat()
      (requestMap["WBC Count"] as HashMap<String, Float>)[msreDt] = it.wbc!!.toFloat()
      (requestMap["CRP"] as HashMap<String, Float>)[msreDt] = it.crp!!.toFloat()
      (requestMap["BDTEMP"] as HashMap<String, Float>)[msreDt] = it.bdtp!!.toFloat()
      (requestMap["BREATH"] as HashMap<String, Float>)[msreDt] = it.resp!!.toFloat()
      (requestMap["DBP"] as HashMap<String, Float>)[msreDt] = it.dbp!!.toFloat()
      (requestMap["PULSE"] as HashMap<String, Float>)[msreDt] = it.hr!!.toFloat()
      (requestMap["SBP"] as HashMap<String, Float>)[msreDt] = it.sbp!!.toFloat()
      (requestMap["SPO2"] as HashMap<String, Float>)[msreDt] = it.spo2!!.toFloat()

    }
    val json = ObjectMapper().writeValueAsString(requestMap)

    return JSONObject.quote(json.toString())
  }

  @Transactional
  fun saveSvrtAnly(pid: String) {
    val svrtCollList = svrtCollRepository.findByPidOrderByMsreDtAsc(pid)
    val filteredSvrtCollList = svrtCollList.filter { !it.isMntrInfoValueBlank() } // 2
    val covSfList = svrtAnlyHandler.analyseV4(pid, filteredSvrtCollList) // 5

    covSfList.forEachIndexed { idx, covSf ->
      val svrtColl = filteredSvrtCollList.getOrElse(idx) { filteredSvrtCollList.last() }
      val svrtAnlyId = SvrtAnlyId(
        ptId = svrtColl.id.ptId,
        hospId = svrtColl.id.hospId,
        rgstSeq = svrtColl.id.rgstSeq,
        msreDt = svrtColl.id.msreDt,
        collSeq = svrtColl.id.collSeq,
        anlyDt = StringUtils.getYyyyMmDd(),
        anlySeq = idx + 1,
      )
      val svrtAnly = SvrtAnly(
        id = svrtAnlyId,
        pid = pid,
        collDt = StringUtils.convertInstantToYyyyMMdd(svrtColl.rgstDttm),
        collTm = StringUtils.convertInstantToHhmmss(svrtColl.rgstDttm),
        anlyTm = StringUtils.getHhMmSs(),
        covSf = covSf.toString(),
        prdtDt = if (idx >= filteredSvrtCollList.size) StringUtils.getYyyyMmDd()
          .plusDays(idx - filteredSvrtCollList.size + 1) else null,
      )
      svrtAnlyRepository.persist(svrtAnly)
    }
  }

  fun getLastAnlySeqValue(): Int? {
    return svrtAnlyRepository.getLastAnlySeqValue()
  }

  fun saveSvrtColl(coll: SvrtColl) {
    svrtCollRepository.persist(coll)
  }

  fun deleteSvrtCollById(id: SvrtCollId): Boolean {
    return svrtCollRepository.deleteById(id)
  }

  fun findSeverityInfos(ptId: String): CommonResponse<List<SvrtColl>> {
    return CommonResponse(svrtCollRepository.findAllByPtIdOrderByCollSeqAsc(ptId))
  }

  @Transactional
  fun saveFatimaMntrInfoWithSample(pid: String): SvrtColl? {
    val svrtPt = svrtPtRepository.findByPid(pid) ?: return null

    // 관찰 종료일이 없는 경우에만 수집
    if (svrtPt.monEndDt == null) {
      val svrtColls = svrtCollRepository.findByPidAndHospId(pid, svrtPt.id.hospId)
      val basedd = svrtColls.lastOrNull()?.id?.msreDt?.plusDays(1) ?: svrtPt.monStrtDt

      val sampleData = HisRestClientRequest(pid, basedd)
      val fatimaSvrtMntrInfo = fatimaHisRestClient.getFatimaSvrtMntrInfo(sampleData)
      log.debug("fatimaSvrtMntrInfo: $fatimaSvrtMntrInfo")

      // 리스트가 비어있거나, 마지막 데이터의 msreDt가 basedd와 다르면 endMonitoring
      if (fatimaSvrtMntrInfo.body.isEmpty() || fatimaSvrtMntrInfo.body.last().msreDt != basedd) {
        svrtPt.endMonitoring(basedd, StringUtils.getHhMmSs())
        return null
      }

      val svrtMntrInfo = fatimaSvrtMntrInfo.body.last()
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
  fun saveKnuhMntrInfoWithSample(pid: String) {
    val svrtPt = svrtPtRepository.findByPid(pid) ?: return
    val svrtColls = svrtCollRepository.findByPidAndHospId(pid, svrtPt.id.hospId)
    val basedd = svrtColls.lastOrNull()?.id?.msreDt?.plusDays(1) ?: svrtPt.monStrtDt

    val sampleData = HisRestClientRequest(pid, basedd)
    val knuhSvrtMntrInfo = knuhHisRestClient.getKnuhSvrtMntrInfo(sampleData)
    log.debug("knuhSvrtMntrInfo: $knuhSvrtMntrInfo")

    if (knuhSvrtMntrInfo.body.isEmpty()) {
      svrtPt.endMonitoring(StringUtils.getYyyyMmDd(), StringUtils.getHhMmSs())
      return
    }

    val svrtMntrInfo = knuhSvrtMntrInfo.body.last()
    val svrtColl = svrtMntrInfo.toSvrtColl(
      ptId = svrtPt.id.ptId,
      hospId = svrtPt.id.hospId,
      rgstSeq = svrtPt.id.rgstSeq,
      collSeq = svrtColls.lastOrNull()?.id?.collSeq?.plus(1) ?: 1,
    )
    svrtCollRepository.persist(svrtColl)
  }

  @Transactional
  fun saveKnuchMntrInfoWithSample(pid: String) {
    val svrtPt = svrtPtRepository.findByPid(pid) ?: return
    val svrtColls = svrtCollRepository.findByPidAndHospId(pid, svrtPt.id.hospId)
    val basedd = svrtColls.lastOrNull()?.id?.msreDt?.plusDays(1) ?: svrtPt.monStrtDt

    val sampleData = HisRestClientRequest(pid, basedd)
    val knuchSvrtMntrInfo = knuhHisRestClient.getKnuchSvrtMntrInfo(sampleData)
    log.debug("knuchSvrtMntrInfo: $knuchSvrtMntrInfo")

    if (knuchSvrtMntrInfo.body.isEmpty()) {
      svrtPt.endMonitoring(StringUtils.getYyyyMmDd(), StringUtils.getHhMmSs())
      return
    }

    val svrtMntrInfo = knuchSvrtMntrInfo.body.last()
    val svrtColl = svrtMntrInfo.toSvrtColl(
      ptId = svrtPt.id.ptId,
      hospId = svrtPt.id.hospId,
      rgstSeq = svrtPt.id.rgstSeq,
      collSeq = svrtColls.lastOrNull()?.id?.collSeq?.plus(1) ?: 1,
    )
    svrtCollRepository.persist(svrtColl)
  }
}