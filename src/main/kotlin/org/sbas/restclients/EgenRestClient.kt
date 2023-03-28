package org.sbas.restclients

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.ws.rs.*
import javax.ws.rs.core.MediaType


/**
 * E-GEN API를 처리하는 클라이언트
 */
@Path("B552657")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient
interface EgenRestClient {

    /**
     * 코드 마스터 정보 조회
     */
    @GET
    @Path("CodeMast/info")
    fun getCodeMastInfo(@QueryParam("serviceKey") serviceKey: String, @QueryParam("CM_MID") cmMid: String,
                        @QueryParam("pageNo") pageNo: String, @QueryParam("numOfRows") numOfRows: String): String

    /**
     * 병‧의원 목록정보 조회
     */
    @GET
    @Path("HsptlAsembySearchService/getHsptlMdcncListInfoInqire")
    fun getHsptlMdcncListInfoInqire(@QueryParam("serviceKey") serviceKey: String,
                                    @QueryParam("Q0") q0: String?,
                                    @QueryParam("Q1") q1: String?,
                                    @QueryParam("QZ") qz: String?,
                                    @QueryParam("QD") qd: String?,
                                    @QueryParam("QT") qt: String?,
                                    @QueryParam("QN") qn: String?,
                                    @QueryParam("ORD") ord: String?,
                                    @QueryParam("pageNo") pageNo: String?,
                                    @QueryParam("numOfRows") numOfRows: String?) : String

    /**
     * 병‧의원 위치정보 조회
     */
    @GET
    @Path("HsptlAsembySearchService/getHsptlMdcncLcinfoInqire")
    fun getHsptlMdcncLcinfoInqire(@QueryParam("serviceKey") serviceKey: String, @QueryParam("WGS84_LON") wgs84Lon: String?,
                                  @QueryParam("WGS84_LAT") wgs84Lat: String?, @QueryParam("pageNo") pageNo: String?,
                                  @QueryParam("numOfRows") numOfRows: String?): String

    /**
     * 병‧의원별 기본정보 조회
     */
    @GET
    @Path("HsptlAsembySearchService/getHsptlBassInfoInqire")
    fun getHsptlBassInfoInqire(@QueryParam("serviceKey") serviceKey: String, @QueryParam("HPID") hpId: String?,
                               @QueryParam("pageNo") pageNo: String?, @QueryParam("numOfRows") numOfRows: String?): String

    /**
     * 응급실 실시간 가용병상정보 조회
     */
    @GET
    @Path("ErmctInfoInqireService/getEmrrmRltmUsefulSckbdInfoInqire")
    fun getEmrrmRltmUsefulSckbdInfoInqire(@QueryParam("serviceKey") serviceKey: String,
                                          @QueryParam("STAGE1") stage1: String,
                                          @QueryParam("STAGE2") stage2: String,
                                          @QueryParam("pageNo") pageNo: String?,
                                          @QueryParam("numOfRows") numOfRows: String?): String

    /**
     * 중증질환자 수용가능정보 조회
     */
    @GET
    @Path("ErmctInfoInqireService/getSrsillDissAceptncPosblInfoInqire")
    fun getSrsillDissAceptncPosblInfoInqire(@QueryParam("serviceKey") serviceKey: String,
                                            @QueryParam("STAGE1") stage1: String,
                                            @QueryParam("STAGE2") stage2: String,
                                            @QueryParam("SM_TYPE") smType: String?,
                                            @QueryParam("pageNo") pageNo: String?,
                                            @QueryParam("numOfRows") numOfRows: String?): String

    /**
     * 응급의료기관 목록정보 조회
     */
    @GET
    @Path("ErmctInfoInqireService/getEgytListInfoInqire")
    fun getEgytListInfoInqire(@QueryParam("serviceKey") serviceKey: String,
                              @QueryParam("Q0") q0: String?,
                              @QueryParam("Q1") q1: String?,
                              @QueryParam("QZ") qz: String?,
                              @QueryParam("QD") qd: String?,
                              @QueryParam("QT") qt: String?,
                              @QueryParam("QN") qn: String?,
                              @QueryParam("ORD") ord: String?,
                              @QueryParam("pageNo") pageNo: String?,
                              @QueryParam("numOfRows") numOfRows: String?): String

    /**
     * 응급의료기관 위치정보 조회
     */
    @GET
    @Path("ErmctInfoInqireService/getEgytLcinfoInqire")
    fun getEgytLcinfoInqire(@QueryParam("serviceKey") serviceKey: String, @QueryParam("WGS84_LON") wgs84Lon: String,
                            @QueryParam("WGS84_LAT") wgs84Lat: String, @QueryParam("pageNo") pageNo: String?,
                            @QueryParam("numOfRows") numOfRows: String?): String

    /**
     * 응급의료기관 기본정보 조회
     */
    @GET
    @Path("ErmctInfoInqireService/getEgytBassInfoInqire")
    fun getEgytBassInfoInqire(@QueryParam("serviceKey") serviceKey: String, @QueryParam("HPID") hpId: String?,
                              @QueryParam("pageNo") pageNo: String?, @QueryParam("numOfRows") numOfRows: String?): String

}