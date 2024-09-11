package org.sbas.endpoints.prvt

import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.BeanParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import org.sbas.dtos.bdas.*
import org.sbas.services.BdasService

@Tag(name = "병상배정 업무처리(사용자 권한용)", description = "로그인 된 사용자(세부권한별 분기) - 병상배정현황 조회 및 처리 등")
@Path("v1/private/bedasgn")
class PrivateBedAssignEndpoint {

  @Inject
  lateinit var log: Logger

  @Inject
  lateinit var bdasService: BdasService

  @Operation(summary = "병상승인/불가 (병상배정반)", description = "")
  @POST
  @Path("reqconfirm")
  fun reqconfirm(@Valid bdasReqAprvSaveRequest: BdasReqAprvSaveRequest): Response {
    log.debug("PrivateBedAssignEndpoint reqconfirm >>> $bdasReqAprvSaveRequest")
    return Response.ok(bdasService.reqConfirm(bdasReqAprvSaveRequest)).build()
  }

  @Operation(summary = "가용 병원 목록 조회", description = "")
  @GET
  @Path("hosp-list/{ptId}/{bdasSeq}")
  fun getHospList(
    @RestPath ptId: String, @RestPath bdasSeq: Int,
    @BeanParam avalHospListRequest: AvalHospListRequest,
  ): Response {
    log.debug("PrivateBedAssignEndpoint getHospList >>> $ptId, $bdasSeq, $avalHospListRequest")
    return Response.ok(bdasService.getAvalHospList(ptId, bdasSeq, avalHospListRequest)).build()
  }

  @Operation(summary = "배정승인/불가 (의료진)", description = "")
  @POST
  @Path("asgnconfirm")
  fun asgnconfirm(@Valid bdasAprvSaveRequest: BdasAprvSaveRequest): Response {
    log.debug("PrivateBedAssignEndpoint asgnconfirm >>> $bdasAprvSaveRequest")
    return Response.ok(bdasService.asgnConfirm(bdasAprvSaveRequest)).build()
  }

  @Operation(summary = "이송/배차 처리 (+구급대, 구급대원 및 차량번호 등록)", description = "")
  @POST
  @Path("confirmtransf")
  fun confirmtransf(@Valid bdasTrnsSaveRequest: BdasTrnsSaveRequest): Response {
    log.debug("PrivateBedAssignEndpoint confirmtransf >>> $bdasTrnsSaveRequest")
    return Response.ok(bdasService.confirmTrans(bdasTrnsSaveRequest)).build()
  }

  @Operation(summary = "입/퇴원/재택회송 처리", description = "")
  @POST
  @Path("confirmhosptlzdiscg")
  fun confirmhosptlzdiscg(@Valid bdasAdmsSaveRequest: BdasAdmsSaveRequest): Response {
    log.debug("PrivateBedAssignEndpoint confirmhosptlzdiscg >>> $bdasAdmsSaveRequest")
    return Response.ok(bdasService.confirmHosp(bdasAdmsSaveRequest)).build()
  }

  @Operation(summary = "병상배정 목록 (상태별)", description = "")
  @GET
  @Path("list")
  fun list(@BeanParam param: BdasListSearchParam): Response {
    return Response.ok(bdasService.findBedAsgnList(param)).build()
  }

  @Operation(summary = "병상배정 목록(웹)", description = "")
  @GET
  @Path("list-web")
  fun listForWeb(@BeanParam param: BdasListSearchParam): Response? {
    try {
      return Response.ok(bdasService.findBedAsgnListForWeb(param)).build()
    } catch(e: Exception) {
      e.printStackTrace()
    }
    return null
  }

  @Operation(summary = "병상배정코드 카운트(웹)", description = "")
  @GET
  @Path("bedstat-count")
  fun countBedStat(@BeanParam param: BdasListSearchParam): Response {
    return Response.ok(bdasService.countBedStatCd(param)).build()
  }

  @GET
  @Path("remove/{ptId}/{step}")
  fun remove(@RestPath ptId: String?, @RestPath step: Int?): Response {
    return Response.ok(bdasService.removeData(ptId, step)).build()
  }
}