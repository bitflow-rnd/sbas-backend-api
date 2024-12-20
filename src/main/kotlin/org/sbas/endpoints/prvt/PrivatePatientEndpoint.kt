package org.sbas.endpoints.prvt

import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.BeanParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.dtos.bdas.BdasEsvySaveRequest
import org.sbas.dtos.bdas.BdasReqSaveRequest
import org.sbas.dtos.info.BdasHospListRequest
import org.sbas.dtos.info.InfoPtCheckRequest
import org.sbas.dtos.info.InfoPtDto
import org.sbas.dtos.info.InfoPtSearchParam
import org.sbas.parameters.NewsScoreParameters
import org.sbas.services.BdasService
import org.sbas.services.PatientService


@Tag(name = "환자 관리(사용자 권한용)", description = "로그인 된 사용자(세부권한별 분기) - 환자 등록 및 조회 등")
@Path("v1/private/patient")
class PrivatePatientEndpoint {

  @Inject
  lateinit var log: Logger

  @Inject
  lateinit var patientService: PatientService

  @Inject
  lateinit var bdasService: BdasService

  @Operation(summary = "역학조사서 업로드/업데이트", description = "")
  @POST
  @Path("upldepidreport")
  fun upldepidreport(@RestForm param1: String, @RestForm param2: FileUpload): Response {
    val res = patientService.uploadEpidReport(param2)
    return Response.ok(res).build()
  }

  @Operation(summary = "역학조사서 삭제", description = "")
  @POST
  @Path("delepidreport/{attcId}")
  fun delepidreport(@RestPath attcId: String): Response {
    return Response.ok(patientService.delEpidReport(attcId)).build()
  }

  @Operation(summary = "역학조사서 읽어오기", description = "")
  @GET
  @Path("read-epidreport/{attcId}")
  fun readEpidReport(@RestPath attcId: String): Response {
    return Response.ok(patientService.readEpidReport(attcId)).build()
  }

  @Operation(summary = "환자 기본정보 등록", description = "")
  @POST
  @Path("regbasicinfo")
  fun regbasicinfo(@Valid infoPtDto: InfoPtDto): Response {
    return Response.ok(patientService.saveInfoPt(infoPtDto)).build()
  }

  @Operation(summary = "환자 중복 유효성 검사", description = "")
  @POST
  @Path("exist")
  fun exist(checker: InfoPtCheckRequest): Response {
    return Response.ok(patientService.checkInfoPt(checker)).build()
  }

  @Operation(summary = "환자정보 수정", description = "")
  @POST
  @Path("modinfo/{ptId}")
  fun modinfo(@RestPath ptId: String, @RequestBody infoPtDto: InfoPtDto): Response {
    log.debug("modinfo============>>>>>>> $infoPtDto")
    return Response.ok(patientService.updateInfoPt(ptId, infoPtDto)).build()
  }

  @Operation(summary = "환자 기본정보 조회", description = "")
  @GET
  @Path("basicinfo/{ptId}")
  fun basicinfo(@RestPath("ptId") ptId: String): Response {
    return Response.ok(patientService.findBasicInfo(ptId)).build()
  }

  @Operation(summary = "환자 질병정보 조회", description = "")
  @GET
  @Path("esvyinfo/{ptId}")
  fun esvyInfo(@RestPath ptId: String): Response {
    return Response.ok(patientService.findEsvyInfo(ptId)).build()
  }

  @Operation(summary = "병상배정 이력 목록 조회", description = "")
  @GET
  @Path("bdasHisinfos/{ptId}")
  fun bdasHisinfos(@RestPath("ptId") ptId: String): Response {
    return Response.ok(patientService.findBdasHistInfo(ptId)).build()
  }

  @Operation(summary = "환자 병상배정 차수별 상세내용(타임라인) 조회", description = "")
  @GET
  @Path("timeline/{ptId}/{bdasSeq}")
  fun timeline(@RestPath ptId: String, @RestPath bdasSeq: Int): Response {
    return Response.ok(bdasService.getTimeLine(ptId, bdasSeq)).build()
  }

  @Operation(summary = "차수별 질병(감염병) 정보 조회", description = "")
  @GET
  @Path("disease-info/{ptId}")
  fun getDiseaseInfo(@RestPath ptId: String): Response? {
    return Response.ok(bdasService.getDiseaseInfo(ptId)).build()
  }

  @Operation(summary = "차수별 중증정보 조회", description = "")
  @GET
  @Path("sevrinfo/{ptId}/{bdasSeq}")
  fun sevrinfo(@RestPath ptId: String, @RestPath bdasSeq: String): Response? {
    return Response.ok().build()
  }

  @Operation(summary = "차수별 이송정보 조회", description = "환자 ID와 배정 순번으로 해당 이송정보 조회")
  @GET
  @Path("transinfo/{ptId}/{bdasSeq}")
  fun transInfo(@RestPath ptId: String, @RestPath bdasSeq: Int): Response {
    return Response.ok(bdasService.findTransInfo(ptId, bdasSeq)).build()
  }

  @Operation(summary = "질병(감염병) 정보 등록", description = "")
  @POST
  @Path("regdisesinfo")
  fun regdisesinfo(bdasEsvySaveRequest: BdasEsvySaveRequest): Response? {
    return Response.ok(bdasService.regDisesInfo(bdasEsvySaveRequest)).build()
  }

  @Operation(summary = "생체정보입력 분석", description = "생체정보 입력 시 경북대학교 로직(NEWS Score)에 따른 중증분류 값 리턴")
  @POST
  @Path("bioinfoanlys")
  fun bioinfoanlys(param: NewsScoreParameters): Response {
    return Response.ok(patientService.calculateNewsScore(param)).build()
  }

  @Operation(summary = "병상 배정 요청", description = "병상 배정 요청 (중증 정보 + 출발지 정보)")
  @POST
  @Path("bedassignreq")
  fun bedassignreq(@Valid bdasReqSaveRequest: BdasReqSaveRequest): Response? {
    log.debug("bedassignreq >>> $bdasReqSaveRequest")
    val res = bdasService.registerBedRequestInfo(bdasReqSaveRequest)
    return Response.ok(res).build()
  }

  @Operation(summary = "현재 차수 병상배정 관련 타임라인 메시지 전송", description = "")
  @POST
  @Path("sendmsg/{param}")
  fun sendmsg(@RestPath param: String): Response? {
    return Response.ok().build()
  }

  @Operation(summary = "전국 환자검색", description = "검색 조건에 맞는 환자 목록 조회")
  @GET
  @Path("search")
  fun search(@BeanParam @Valid param: InfoPtSearchParam): Response {
    log.debug("searchParam: $param")
    return Response.ok(patientService.findInfoPtList(param)).build()
  }

  @Operation(summary = "전국 환자검색", description = "검색 조건에 맞는 환자 목록 조회")
  @GET
  @Path("search-mobile")
  fun searchMobile(@BeanParam @Valid param: InfoPtSearchParam): Response {
    log.debug("searchParam: $param")
    return Response.ok(patientService.findInfoPtListMobile(param)).build()
  }

  @Operation(summary = "", description = "")
  @GET
  @Path("searchhosps")
  fun searchhosps(@BeanParam @Valid param: InfoPtSearchParam): Response {
    return Response.ok(patientService.findHospNmList(param)).build()
  }

  @Operation(summary = "내 기관 관련 환자목록", description = "")
  @GET
  @Path("myorganiztn")
  fun myorganiztn(): Response {
    return Response.ok(patientService.findInfoPtWithMyOrgan()).build()
  }

  @Operation(summary = "배정 중인 병원 목록", description = "현재 배정 중인 병원 목록 조회")
  @GET
  @Path("bdas-hosp")
  fun getBdasHospList(@BeanParam param: BdasHospListRequest): Response {
    return Response.ok(patientService.getBdasHospList(param)).build()
  }
}