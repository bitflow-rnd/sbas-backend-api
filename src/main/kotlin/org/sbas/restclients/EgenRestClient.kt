package org.sbas.restclients

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.sbas.restresponses.EgenResponse
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType


/**
 * E-GEN API를 처리하는 클라이언트
 */
@Path("/B552657")
@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_XML)
@RegisterRestClient
interface EgenRestClient {

    /**
     * 코드 마스터 정보 조회
     */
    @GET
    @Path("/CodeMast/info")
    fun getCodeMastInfo(@QueryParam("serviceKey") serviceKey: String, @QueryParam("CM_MID") CM_MID: String): EgenResponse

    /**
     * 병‧의원 목록정보 조회
     */
    @GET
    @Path("/HsptlAsembySearchService/getHsptlMdcncListInfoInqire")
    fun getHsptlMdcncListInfoInqire(@QueryParam("serviceKey") serviceKey: String, @QueryParam("CM_MID") CM_MID: String): EgenResponse

    /**
     * 병‧의원 위치정보 조회
     */
    @GET
    @Path("/HsptlAsembySearchService/getHsptlMdcncLcinfoInqire")
    fun getHsptlMdcncLcinfoInqire(@QueryParam("serviceKey") serviceKey: String, @QueryParam("CM_MID") CM_MID: String): EgenResponse

    /**
     * 병‧의원별 기본정보 조회
     */
    @GET
    @Path("/HsptlAsembySearchService/getHsptlBassInfoInqire")
    fun getHsptlBassInfoInqire(@QueryParam("serviceKey") serviceKey: String, @QueryParam("CM_MID") CM_MID: String): EgenResponse

    /**
     * 응급실 실시간 가용병상정보 조회
     */
    @GET
    @Path("/ErmctInfoInqireService/getEmrrmRltmUsefulSckbdInfoInqire")
    fun getEmrrmRltmUsefulSckbdInfoInqire(@QueryParam("serviceKey") serviceKey: String, @QueryParam("CM_MID") CM_MID: String): EgenResponse

    /**
     * 중증질환자 수용가능정보 조회
     */
    @GET
    @Path("/ErmctInfoInqireService/getSrsillDissAceptncPosblInfoInqire")
    fun getSrsillDissAceptncPosblInfoInqire(@QueryParam("serviceKey") serviceKey: String, @QueryParam("CM_MID") CM_MID: String): EgenResponse

    /**
     * 응급의료기관 목록정보 조회
     */
    @GET
    @Path("/ErmctInfoInqireService/getEgytListInfoInqire")
    fun getEgytListInfoInqire(@QueryParam("serviceKey") serviceKey: String, @QueryParam("CM_MID") CM_MID: String): EgenResponse

    /**
     * 응급의료기관 위치정보 조회
     */
    @GET
    @Path("/ErmctInfoInqireService/getEgytLcinfoInqire")
    fun getEgytLcinfoInqire(@QueryParam("serviceKey") serviceKey: String, @QueryParam("CM_MID") CM_MID: String): EgenResponse

    /**
     * 응급의료기관 기본정보 조회
     */
    @GET
    @Path("/ErmctInfoInqireService/getEgytBassInfoInqire")
    fun getEgytBassInfoInqire(@QueryParam("serviceKey") serviceKey: String, @QueryParam("CM_MID") CM_MID: String): EgenResponse

}