package org.sbas.endpoints.admin

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.sbas.constants.EgenCmMid
import org.sbas.restparameters.EgenApiBassInfoParams
import org.sbas.restparameters.EgenApiEmrrmRltmUsefulSckbdInfoParams
import org.sbas.restparameters.EgenApiLcInfoParams
import org.sbas.restparameters.EgenApiListInfoParams
import org.sbas.services.EgenService
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Tag(name = "E-Gen 연동 관리(어드민 권한용)", description = "System Admin 사용자 - E-Gen API를 통한 데이터 동기화, 등록, 수정, 삭제 등")
//@RolesAllowed("ADMIN")
@Path("v1/admin/egen")
class AdminEgenEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var egenService: EgenService

    @Operation(summary = "E-GEN 의료기관 정보 동기화", description = "")
    @POST
    @Path("syncmedinsts")
    fun syncMedInsts(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "E-GEN 실시간가용병상 정보 동기화", description = "")
    @POST
    @Path("syncrealtmavailbeds")
    fun syncRealTmAvailBeds(param: EgenApiEmrrmRltmUsefulSckbdInfoParams): Response {
        //TODO
        param.numOfRows = param.numOfRows ?: "100"
        return Response.ok(egenService.saveUsefulSckbdInfo(param)).build()
    }

    @Operation(summary = "E-GEN 공통코드 동기화", description = "")
    @POST
    @Path("synccommcodes")
    fun syncCommCodes(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "E-GEN 코드마스터 정보 조회", description = "E-GEN 코드마스터 정보 조회")
    @GET
    @Path("getCodeMastInfo/{cmMid}")
    fun getCodeMastInfo(cmMid: EgenCmMid): Response {
        val res = egenService.getCodeMastInfo(cmMid).toString()
        return makeToSuccessResponse(res)
    }

    @Operation(summary = "E-GEN 의료기관 정보 동기화(수동)", description = "관리자 버튼 클릭으로 동기화 시작")
    @POST
    @Path("hsptlMdcncList")
    fun getHsptlMdcncList(param: EgenApiListInfoParams): Response {
        val res = egenService.saveHsptlMdcncList(param)
        return Response.ok(res).type(MediaType.APPLICATION_JSON_TYPE).build()
    }

    @Operation(summary = "병‧의원 위치정보 조회", description = "")
    @GET
    @Path("hsptlMdcncLcInfo")
    fun getHsptlMdcncLcInfo(param: EgenApiLcInfoParams): Response {
        val res = egenService.getHsptlMdcncLcinfoInqire(param).toString()
        return makeToSuccessResponse(res)
    }

    @Operation(summary = "병의원 기본정보 조회", description = "")
    @GET
    @Path("hsptlBassInfo")
    fun getHsptlBassInfo(param: EgenApiBassInfoParams): Response {
        val res = egenService.getHsptlBassInfoInqire(param).toString()
        return makeToSuccessResponse(res)
    }

    @Operation(summary = "응급의료기관 목록정보 조회", description = "")
    @GET
    @Path("egytList")
    fun getEgytList(param: EgenApiListInfoParams): Response {
        val res = egenService.getEgytListInfoInqire(param).toString()
        return makeToSuccessResponse(res)
    }

    @Operation(summary = "응급의료기관 위치정보 조회", description = "")
    @GET
    @Path("egytLcInfo")
    fun getEgytLcInfo(param: EgenApiLcInfoParams): Response {
        val res = egenService.getEgytLcinfoInqire(param).toString()
        return makeToSuccessResponse(res)
    }

    @Operation(summary = "응급의료기관 기본정보 조회", description = "")
    @GET
    @Path("egytBassInfo")
    fun getEgytBassInfo(param: EgenApiBassInfoParams): Response {
        val res = egenService.getEgytBassInfoInqire(param).toString()
        return makeToSuccessResponse(res)
    }

    @Operation(summary = "E-GEN 코드 목록 DB에 저장", description = "")
    @GET
    @Path("saveegencode")
    fun saveEgenCode() {
        egenService.saveEgenCode()
    }

    /**
     * 조회 결과를 Response 객체로 만들어주는 function
     */
    private fun makeToSuccessResponse(res: String): Response =
        Response.ok(Response.Status.OK)
            .entity(res)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .build()
}
