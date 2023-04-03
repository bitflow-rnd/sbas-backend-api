package org.sbas.endpoints.private

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.constants.SbasConst
import org.sbas.dtos.InfoPtReq
import org.sbas.dtos.NewsScoreParam
import org.sbas.responses.CommonResponse
import org.sbas.responses.StringResponse
import org.sbas.responses.patient.EpidResult
import org.sbas.services.CommonService
import org.sbas.services.PatientService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "환자 관리(사용자 권한용)", description = "로그인 된 사용자(세부권한별 분기) - 환자 등록 및 조회 등")
@Path("v1/private/patient")
class PrivatePatientEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var patientService: PatientService

    @Inject
    lateinit var service1: CommonService

    @Operation(summary = "", description = "")
    @POST
    @Path("upldepidreport")
    fun upldepidreport(@RestForm param1: String, @RestForm param2: FileUpload):
            RestResponse<CommonResponse<EpidResult>?> {
        val res = patientService.uploadEpidReport(param2)
        return ResponseBuilder.ok(res).build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("delepidreport")
    fun delepidreport():  Response {
        return Response.ok().build()
    }

    @Operation(summary = "환자기본정보 등록", description = "")
    @POST
    @Path("regbasicinfo")
    fun regbasicinfo(infoPtReq: InfoPtReq): Response {
        return Response.ok(patientService.saveInfoPt(infoPtReq)).build()
    }

    @Operation(summary = "환자 중복 유효성 검사", description = "")
    @POST
    @Path("exist")
    fun exist(infoPtReq: InfoPtReq): CommonResponse<*> {
        val res = patientService.check(infoPtReq) ?: "신규 등록 환자"
        return CommonResponse(SbasConst.ResCode.SUCCESS, "success", res)
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("modinfo")
    fun modinfo():  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("basicinfo/{param}")
    fun basicinfo(@RestPath param: String):  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("timeline/{param1}/{param2}")
    fun timeline(@RestPath param1: String, @RestPath param2: String):  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("disesinfo/{param1}/{param2}")
    fun disesinfo(@RestPath param1: String, @RestPath param2: String):  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("sevrinfo/{param1}/{param2}")
    fun sevrinfo(@RestPath param1: String, @RestPath param2: String):  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "생체정보입력 분석", description = "생체정보 입력 시 경북대학교 로직(NEWS Score)에 따른 중증분류 값 리턴")
    @POST
    @Path("bioinfoanlys")
    //TODO 테이블(svrt_anly)에 값들 저장
    fun bioinfoanlys(param: NewsScoreParam): RestResponse<Int>? {
        patientService.calculateNewsScore(param)
        return ResponseBuilder.ok(patientService.calculateNewsScore(param)).build()
    }

    @Operation(summary = "질병(감염병) 정보 등록", description = "")
    @POST
    @Path("regdisesinfo")
    fun regdisesinfo():  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "중증정보 등록", description = "")
    @POST
    @Path("regsevrinfo")
    fun regsevrinfo():  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "출발지정보 등록 (병상 요청 완료)", description = "")
    @POST
    @Path("regstrtpoint")
    fun regstrtpoint():  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("sendmsg/{param}")
    fun sendmsg(@RestPath param: String):  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "전국 환자검색", description = "")
    @POST
    @Path("search")
    fun search(): Response {
        val findInfoPt = patientService.findInfoPt()
        return Response.ok(findInfoPt).build()
    }

    @Operation(summary = "내 기관 관련 환자목록", description = "")
    @POST
    @Path("myorganiztn")
    fun myorganiztn(): Response {
        return Response.ok(patientService.findInfoPtWithMyOrgan()).build()
    }
}