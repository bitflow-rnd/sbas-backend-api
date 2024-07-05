package org.sbas.services

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.sbas.constants.EgenCmMid
import org.sbas.constants.enums.SidoCd
import org.sbas.dtos.BaseCodeEgenSaveReq
import org.sbas.dtos.info.InfoBedSaveReq
import org.sbas.dtos.info.InfoHospSaveReq
import org.sbas.dtos.toEntity
import org.sbas.repositories.*
import org.sbas.responses.CommonResponse
import org.sbas.restclients.EgenRestClient
import org.sbas.restdtos.EgenApiBassInfoParams
import org.sbas.restdtos.EgenApiEmrrmRltmUsefulSckbdInfoParams
import org.sbas.restdtos.EgenApiLcInfoParams
import org.sbas.restdtos.EgenApiListInfoParams
import org.sbas.utils.CustomizedException


/**
 * E-GEN API를 사용하는 서비스
 */
@ApplicationScoped
class EgenService {

  @Inject
  lateinit var log: Logger

  @Inject
  private lateinit var baseCodeEgenRepository: BaseCodeEgenRepository

  @Inject
  private lateinit var infoHospRepository: InfoHospRepository

  @Inject
  private lateinit var infoBedRepository: InfoBedRepository

  @Inject
  private lateinit var baseCodeRepository: BaseCodeRepository

  @RestClient
  private lateinit var egenRestClient: EgenRestClient

  @ConfigProperty(name = "restclient.egen.service.key")
  private lateinit var serviceKey: String

  @Inject
  private lateinit var objectMapper: ObjectMapper

  /**
   * 코드 마스터 정보 조회
   */
  fun getCodeMastInfo(cmMid: EgenCmMid): JSONObject {
    val jsonObject = JSONObject(
      egenRestClient.getCodeMastInfo(
        serviceKey = serviceKey,
        cmMid = cmMid.name,
        pageNo = "1",
        numOfRows = "2000"
      )
    )
    return extractBody(jsonObject)
  }

  /**
   * 병‧의원 목록정보 조회 및 저장
   */
  fun getHsptlMdcncListInfoInqire(param: EgenApiListInfoParams): Pair<JSONObject, Int> {
    val jsonObject = JSONObject(
      egenRestClient.getHsptlMdcncListInfoInqire(
        serviceKey = serviceKey,
        q0 = param.q0, q1 = param.q1,
        qz = param.qz, qd = param.qd,
        qt = param.qt, qn = param.qn,
        ord = param.ord, pageNo = param.pageNo, numOfRows = param.numOfRows
      )
    )
    return Pair(extractBody(jsonObject), extractTotalCount(jsonObject))
  }

  /**
   * 병‧의원 위치정보 조회
   */
  fun getHsptlMdcncLcinfoInqire(param: EgenApiLcInfoParams): JSONObject {
    val jsonObject = JSONObject(
      egenRestClient.getHsptlMdcncLcinfoInqire(
        serviceKey = serviceKey,
        wgs84Lon = param.wgs84Lon,
        wgs84Lat = param.wgs84Lat,
        pageNo = param.pageNo,
        numOfRows = param.numOfRows,
      )
    )
    return extractBody(jsonObject)
  }

  /**
   * 병‧의원별 기본정보 조회
   */
  fun getHsptlBassInfoInqire(param: EgenApiBassInfoParams): JSONObject {
    val jsonObject = JSONObject(
      egenRestClient.getHsptlBassInfoInqire(
        serviceKey = serviceKey,
        hpId = param.hpId,
        pageNo = param.pageNo,
        numOfRows = param.numOfRows
      )
    )

    return extractBody(jsonObject)
  }

  /**
   * 응급실 실시간 가용병상정보 조회
   */
  fun getEmrrmRltmUsefulSckbdInfoInqire(param: EgenApiEmrrmRltmUsefulSckbdInfoParams): Pair<JSONObject, Int> {
    val jsonObject = JSONObject(
      egenRestClient.getEmrrmRltmUsefulSckbdInfoInqire(
        serviceKey = serviceKey,
        stage1 = param.stage1,
        stage2 = param.stage2,
        pageNo = param.pageNo,
        numOfRows = param.numOfRows,
      )
    )
    return Pair(extractBody(jsonObject), extractTotalCount(jsonObject))
  }

  /**
   * 응급의료기관 목록정보 조회
   */
  fun getEgytListInfoInqire(param: EgenApiListInfoParams): JSONObject {
    val jsonObject = JSONObject(
      egenRestClient.getEgytListInfoInqire(
        serviceKey = serviceKey,
        q0 = "", q1 = "", qz = "",
        qd = "", qt = "", qn = "",
        ord = "", pageNo = "", numOfRows = ""
      )
    )
    return extractBody(jsonObject)
  }

  /**
   * 응급의료기관 위치정보 조회
   */
  fun getEgytLcinfoInqire(param: EgenApiLcInfoParams): JSONObject {
    val jsonObject = JSONObject(
      egenRestClient.getEgytLcinfoInqire(
        serviceKey = serviceKey,
        wgs84Lon = "127.0851566",
        wgs84Lat = "37.48813256",
        pageNo = "1",
        numOfRows = "1",
      )
    )
    return extractBody(jsonObject)
  }

  /**
   * 응급의료기관 기본정보 조회
   */
  fun getEgytBassInfoInqire(param: EgenApiBassInfoParams): JSONObject {
    val jsonObject = JSONObject(
      egenRestClient.getEgytBassInfoInqire(
        serviceKey = serviceKey,
        hpId = "A1100001",
        pageNo = "1",
        numOfRows = "10"
      )
    )
    return extractBody(jsonObject)
  }

  /**
   * E-GEN 코드 마스터 정보를 DB에 저장
   */
  @Transactional
  fun saveEgenCode() {
    var res: BaseCodeEgenSaveReq
    EgenCmMid.values().forEach { egenCmMid ->
      val jsonObject = getCodeMastInfo(egenCmMid)
      jsonObject.getJSONArray("item").forEach {
        res = ObjectMapper().readValue(it.toString(), BaseCodeEgenSaveReq::class.java)
        baseCodeEgenRepository.getEntityManager().merge(res.toEntity())
      }
    }
  }

  /**
   * E-GEN 병의원 목록정보를 DB에 저장
   */
  @Transactional
  fun saveHsptlMdcncList(param: EgenApiListInfoParams): CommonResponse<*> {
    var res: InfoHospSaveReq
    val jsonArray = try {
      getHsptlMdcncListInfoInqire(param).first
    } catch (e: Exception) {
      return CommonResponse(e.message)
    }
    jsonArray.getJSONArray("item").forEach {
      res = ObjectMapper().readValue(it.toString(), InfoHospSaveReq::class.java)
      val addr = baseCodeRepository.findCdIdByAddrNm(res.dutyAddr!!)
      infoHospRepository.getEntityManager().merge(res.toEntity(addr.siDoCd, addr.siGunGuCd))
    }
    return CommonResponse(jsonArray)
  }

  fun saveHospitalBedInfos() {

  }

  /**
   * return: 1: success, 0: fail
   */
  @Transactional
  fun syncHospitalInfos(): Boolean {
    var ret = false
    try {
      val jsonObject = JSONObject(
        //                egenRestClient.getHsptlMdcncListInfoInqire(
        //                    serviceKey = serviceKey,
        //                    q0 = param.q0, q1 = param.q1,
        //                    qz = param.qz, qd = param.qd,
        //                    qt = param.qt, qn = param.qn,
        //                    ord = param.ord, pageNo = param.pageNo, numOfRows = param.numOfRows
        //                )
      )
      ret = true
    } catch (e: Exception) {
      log.error("scheduler failed when running, e = ${e.message}")
    }
    return ret
  }

  fun saveUsefulSckbdInfo(param: EgenApiEmrrmRltmUsefulSckbdInfoParams) {
    //        val (jsonObjectIntPair, totalCount) = getEmrrmRltmUsefulSckbdInfoInqire(param)
    SidoCd.entries.forEach {
      if (it.cdNm == "강원도") {
        param.stage1 = "강원특별자치도"
      } else {
        param.stage1 = it.cdNm
      }
      val (jsonObjectIntPair, _) = getEmrrmRltmUsefulSckbdInfoInqire(param)
      val infoJsonArray = jsonObjectIntPair.getJSONArray("item")
      saveInfoBedFromJsonArray(infoJsonArray)
    }
  }

  @Transactional
  fun saveInfoBedFromJsonArray(infoJsonArray: JSONArray) {
    var infoBedSaveReq: InfoBedSaveReq
    infoJsonArray.forEach { jsonObject ->
      infoBedSaveReq = objectMapper.readValue(jsonObject.toString(), InfoBedSaveReq::class.java)
      val infoHospIds = infoHospRepository.findInfoHospByHpId(infoBedSaveReq.hpid)

      val findInfoBed = infoBedRepository.findByHpId(infoHospIds.hpId)
      val newEntity = infoBedSaveReq.toEntity(infoHospIds.hospId)

      if (findInfoBed != null) {
        infoBedRepository.getEntityManager().merge(newEntity)
      } else {
        infoBedRepository.persist(newEntity)
      }
    }
  }

  private fun extractBody(jsonObject: JSONObject): JSONObject {
    val header = jsonObject.getJSONObject("response").getJSONObject("header")
    try {
      return jsonObject.getJSONObject("response").getJSONObject("body").getJSONObject("items")
    } catch (e: JSONException) {
      throw CustomizedException("no items", Response.Status.NOT_FOUND)
    }
  }

  private fun extractTotalCount(jsonObject: JSONObject): Int {
    val header = jsonObject.getJSONObject("response").getJSONObject("header")
    return jsonObject.getJSONObject("response").getJSONObject("body").getInt("totalCount")
  }
}