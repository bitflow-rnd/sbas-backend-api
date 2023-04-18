package org.sbas.endpoints.private

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.dtos.BdasEsvyDto
import org.sbas.dtos.InfoPtDto
import org.sbas.dtos.NewsScoreParam
import org.sbas.parameters.SearchParameters
import org.sbas.services.BedAssignService
import org.sbas.services.CommonService
import org.sbas.services.PatientService
import javax.inject.Inject
import javax.ws.rs.BeanParam
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
    lateinit var commonService: CommonService

    @Inject
    lateinit var bedAssignService: BedAssignService

    @Operation(summary = "역학조사서 업로드/업데이트", description = "")
    @POST
    @Path("upldepidreport")
    fun upldepidreport(@RestForm param1: String, @RestForm param2: FileUpload): Response {
        val res = patientService.uploadEpidReport(param2)
        return Response.ok(res).build()
    }

    @Operation(summary = "역학조사서 삭제", description = "")
    @POST
    @Path("delepidreport")
    fun delepidreport():  Response {
        return Response.ok().build()
    }

    @Operation(summary = "역학조사서 읽어오기", description = "")
    @GET
    @Path("read-epidreport/{attcId}")
    fun readEpidReport(@RestPath attcId: String): Response {
        return Response.ok(patientService.findEpidReportByAttcId(attcId)).build()
    }

    @Operation(summary = "환자기본정보 등록", description = "")
    @POST
    @Path("regbasicinfo")
    fun regbasicinfo(infoPtDto: InfoPtDto): Response {
        return Response.ok(patientService.saveInfoPt(infoPtDto)).build()
    }

    @Operation(summary = "환자 중복 유효성 검사", description = "")
    @POST
    @Path("exist")
    fun exist(infoPtDto: InfoPtDto): Response {
        return Response.ok(patientService.check(infoPtDto)).build()
    }

    @Operation(summary = "환자정보 수정", description = "")
    @POST
    @Path("modinfo/{ptId}")
    fun modinfo(@RestPath ptId: String,@RequestBody infoPtDto: InfoPtDto): Response {
        log.debug("res==============>>>>>>> $infoPtDto")
        return Response.ok(patientService.updateInfoPt(ptId, infoPtDto)).build()
    }

    @Operation(summary = "환자 기본정보 등록", description = "")
    @GET
    @Path("basicinfo/{param}")
    fun basicinfo(@RestPath param: String): Response{
        return Response.ok().build()
    }

    @Operation(summary = "환자 병상배정 차수별 상세내용(타임라인) 조회", description = "")
    @GET
    @Path("timeline/{param1}/{param2}")
    fun timeline(@RestPath param1: String, @RestPath param2: String): Response? {
        return Response.ok().build()
    }

    @Operation(summary = "차수별 질병(감염병) 정보 조회", description = "")
    @GET
    @Path("disesinfo/{param1}/{param2}")
    fun disesinfo(@RestPath param1: String, @RestPath param2: String): Response? {
        return Response.ok().build()
    }

    @Operation(summary = "차수별 중증정보 조회", description = "")
    @GET
    @Path("sevrinfo/{param1}/{param2}")
    fun sevrinfo(@RestPath param1: String, @RestPath param2: String): Response? {
        return Response.ok().build()
    }

    @Operation(summary = "생체정보입력 분석", description = "생체정보 입력 시 경북대학교 로직(NEWS Score)에 따른 중증분류 값 리턴")
    @POST
    @Path("bioinfoanlys")
    fun bioinfoanlys(param: NewsScoreParam): Response {
        patientService.calculateNewsScore(param)
        return Response.ok(patientService.calculateNewsScore(param)).build()
    }

    @Operation(summary = "질병(감염병) 정보 등록", description = "")
    @POST
    @Path("regdisesinfo")
    fun regdisesinfo(bdasEsvyDto: BdasEsvyDto): Response? {
        return Response.ok(bedAssignService.regDisesInfo(bdasEsvyDto)).build()
    }

    @Operation(summary = "중증정보 등록", description = "")
    @POST
    @Path("regsevrinfo")
    fun regsevrinfo(): Response? {
        return Response.ok().build()
    }

    @Operation(summary = "출발지정보 등록 (병상 요청 완료)", description = "")
    @POST
    @Path("regstrtpoint")
    fun regstrtpoint(): Response? {
        return Response.ok().build()
    }

    @Operation(summary = "현재 차수 병상배정 관련 타임라인 메시지 전송", description = "")
    @POST
    @Path("sendmsg/{param}")
    fun sendmsg(@RestPath param: String): Response? {
        return Response.ok().build()
    }

    @Operation(summary = "전국 환자검색", description = "")
    @GET
    @Path("search")
    fun search(@BeanParam searchParameters: SearchParameters): Response {
        log.debug("searchParam: $searchParameters")
        return Response.ok(patientService.findInfoPt2(searchParameters)).build()
    }

    @Operation(summary = "내 기관 관련 환자목록", description = "")
    @GET
    @Path("myorganiztn")
    fun myorganiztn(): Response {
        return Response.ok(patientService.findInfoPtWithMyOrgan()).build()
    }
}