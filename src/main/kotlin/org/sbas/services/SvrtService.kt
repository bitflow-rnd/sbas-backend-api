package org.sbas.services

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.json.JSONObject
import org.sbas.entities.svrt.SvrtAnly
import org.sbas.entities.svrt.SvrtAnlyId
import org.sbas.entities.svrt.SvrtColl
import org.sbas.entities.svrt.SvrtCollId
import org.sbas.repositories.SvrtAnlyRepository
import org.sbas.repositories.SvrtCollRepository
import org.sbas.repositories.SvrtPtRepository
import org.sbas.responses.CommonResponse
import org.sbas.restclients.FatimaHisRestClient
import org.sbas.restclients.HisRestClientRequest
import org.sbas.utils.StringUtils
import org.sbas.utils.StringUtils.Companion.getYyyyMmDdWithHyphen
import org.sbas.utils.plusDays

@ApplicationScoped
class SvrtService(
  private val log: Logger,
  private val svrtAnlyRepository: SvrtAnlyRepository,
  private val svrtCollRepository: SvrtCollRepository,
  private val svrtPtRepository: SvrtPtRepository,
  @RestClient private val fatimaHisRestClient: FatimaHisRestClient,
) {

  fun getSvrtAnlyById(id: SvrtAnlyId): SvrtAnly? {
    return svrtAnlyRepository.findById(id)
  }

  fun getLastSvrtAnlyByPtId(ptId: String): CommonResponse<*> {
    return CommonResponse(svrtAnlyRepository.getSvrtAnlyByPtId(ptId))
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
    return svrtCollRepository.findByPid(pid)
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
    return CommonResponse(svrtCollRepository.findByPtId(ptId))
  }

  @Transactional
  fun saveFatimaMntrInfoWithSample(pid: String) {
    val svrtPt = svrtPtRepository.findByPid(pid) ?: return
    val svrtColls = svrtCollRepository.findByPidAndHospId(pid, svrtPt.id.hospId)
    val basedd = svrtColls.lastOrNull()?.id?.msreDt?.plusDays(1) ?: svrtPt.monStrtDt

    val sampleData = HisRestClientRequest(pid, basedd)
    val fatimaSvrtMntrInfo = fatimaHisRestClient.getFatimaSvrtMntrInfo(sampleData)
    log.debug("fatimaSvrtMntrInfo: $fatimaSvrtMntrInfo")

    if (fatimaSvrtMntrInfo.body.isEmpty()) {
      svrtPt.endMonitoring(StringUtils.getYyyyMmDd(), StringUtils.getHhMmSs())
      return
    }

    val svrtMntrInfo = fatimaSvrtMntrInfo.body.last()
    val svrtColl = svrtMntrInfo.toSvrtColl(
      ptId = svrtPt.id.ptId,
      hospId = svrtPt.id.hospId,
      rgstSeq = svrtPt.id.rgstSeq,
      collSeq = svrtColls.lastOrNull()?.id?.collSeq?.plus(1) ?: 1,
    )
    svrtCollRepository.persist(svrtColl)
  }
}