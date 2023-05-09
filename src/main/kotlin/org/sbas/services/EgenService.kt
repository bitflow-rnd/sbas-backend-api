package org.sbas.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.json.JSONObject
import org.sbas.constants.EgenCmMid
import org.sbas.dtos.BaseCodeEgenSaveReq
import org.sbas.dtos.InfoHospSaveReq
import org.sbas.dtos.toEntity
import org.sbas.repositories.BaseCodeEgenRepository
import org.sbas.repositories.InfoHospRepository
import org.sbas.restclients.EgenRestClient
import org.sbas.restparameters.EgenApiBassInfoParams
import org.sbas.restparameters.EgenApiLcInfoParams
import org.sbas.restparameters.EgenApiListInfoParams
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional


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

    @RestClient
    private lateinit var egenRestClient: EgenRestClient

    @ConfigProperty(name = "restclient.egen.service.key")
    private lateinit var serviceKey: String

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
     * 병‧의원 목록정보 조회
     */
    fun getHsptlMdcncListInfoInqire(param: EgenApiListInfoParams): JSONObject {
        val jsonObject = JSONObject(
            egenRestClient.getHsptlMdcncListInfoInqire(
                serviceKey = serviceKey,
                q0 = param.q0, q1 = param.q1,
                qz = param.qz, qd = param.qd,
                qt = param.qt, qn = param.qn,
                ord = param.ord, pageNo = param.pageNo, numOfRows = param.numOfRows
            )
        )
        return extractBody(jsonObject)
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
    fun getEmrrmRltmUsefulSckbdInfoInqire(): JSONObject {
        val jsonObject = JSONObject(
            egenRestClient.getEmrrmRltmUsefulSckbdInfoInqire(
                serviceKey = serviceKey,
                stage1 = "", stage2 = "",
                pageNo = "",
                numOfRows = ""
            )
        )
        return extractBody(jsonObject)
    }

    /**
     * 중증질환자 수용가능정보 조회
     */
    fun getSrsillDissAceptncPosblInfoInqire(): JSONObject {
        val jsonObject = JSONObject(
            egenRestClient.getSrsillDissAceptncPosblInfoInqire(
                serviceKey = serviceKey,
                stage1 = "", stage2 = "",
                smType = "",
                pageNo = "",
                numOfRows = ""
            )
        )
        return extractBody(jsonObject)
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
        EgenCmMid.values().forEach {
            egenCmMid -> val jsonObject = getCodeMastInfo(egenCmMid)
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
    fun saveHsptlMdcncList(param: EgenApiListInfoParams) {
        var res: InfoHospSaveReq
        val hpIdList = mutableListOf<String>()
        val jsonArray = getHsptlMdcncListInfoInqire(param).getJSONArray("item")
        jsonArray.forEach {
            res = ObjectMapper().readValue(it.toString(), InfoHospSaveReq::class.java)
            log.debug(">>>>>>$res")
            //TODO 반복했을 때 update 되게
            infoHospRepository.persist(res.toEntity())
        }
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

    private fun extractBody(jsonObject: JSONObject): JSONObject {
        val header = jsonObject.getJSONObject("response").getJSONObject("header")
        return jsonObject.getJSONObject("response").getJSONObject("body").getJSONObject("items")
    }

}