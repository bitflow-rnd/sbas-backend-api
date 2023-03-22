package org.sbas.services

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.json.JSONObject
import org.sbas.restclients.EgenRestClient
import org.sbas.restparameters.EgenApiBassInfoParams
import org.sbas.restparameters.EgenApiLcInfoParams
import org.sbas.restparameters.EgenApiListInfoParams
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


/**
 * E-GEN API를 사용하는 서비스
 */
@ApplicationScoped
class EgenService {

    @Inject
    lateinit var log: Logger

    @RestClient
    private lateinit var egenRestClient: EgenRestClient

    @ConfigProperty(name = "restclient.egen.service.key")
    private lateinit var serviceKey: String

    /**
     * 코드 마스터 정보 조회
     */
    fun getCodeMastInfo(): JSONObject {
        val jsonObject = JSONObject(
            egenRestClient.getCodeMastInfo(
                serviceKey = serviceKey,
                cmMid = "",
                pageNo = "",
                numOfRows = ""
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
        return jsonObject
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

    private fun extractBody(jsonObject: JSONObject): JSONObject {
        val header = jsonObject.getJSONObject("response").getJSONObject("header")
        return jsonObject.getJSONObject("response").getJSONObject("body").getJSONObject("items")
    }
}