package org.sbas.endpoints.private

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import org.sbas.dtos.bdas.*
import org.sbas.services.BedAssignService
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.BeanParam
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "병상배정 업무처리(사용자 권한용)", description = "로그인 된 사용자(세부권한별 분기) - 병상배정현황 조회 및 처리 등")
@Path("v1/private/bedasgn")
class PrivateBedAssignEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var bedAssignService: BedAssignService

    @Operation(summary = "병상승인/불가 (병상배정반)", description = "")
    @POST
    @Path("reqconfirm")
    fun reqconfirm(@Valid bdasReqAprvSaveRequest: BdasReqAprvSaveRequest): Response {
        log.debug("PrivateBedAssignEndpoint reqconfirm >>> $bdasReqAprvSaveRequest")
        return Response.ok(bedAssignService.reqConfirm(bdasReqAprvSaveRequest)).build()
    }

    @Operation(summary = "가용 병원 목록 조회", description = "")
    @GET
    @Path("hosp-list/{ptId}/{bdasSeq}")
    fun getHospList(@RestPath ptId: String, @RestPath bdasSeq: Int): Response {
        return Response.ok(bedAssignService.getAvalHospList(ptId, bdasSeq)).build()
    }

    @Operation(summary = "배정승인/불가 (의료진)", description = "")
    @POST
    @Path("asgnconfirm")
    fun asgnconfirm(@Valid bdasAprvSaveRequest: BdasAprvSaveRequest): Response {
        log.debug("PrivateBedAssignEndpoint asgnconfirm >>> $bdasAprvSaveRequest")
        return Response.ok(bedAssignService.asgnConfirm(bdasAprvSaveRequest)).build()
    }

    @Operation(summary = "이송/배차 처리 (+구급대, 구급대원 및 차량번호 등록)", description = "")
    @POST
    @Path("confirmtransf")
    fun confirmtransf(@Valid bdasTrnsSaveRequest: BdasTrnsSaveRequest): Response {
        log.debug("PrivateBedAssignEndpoint confirmtransf >>> $bdasTrnsSaveRequest")
        return Response.ok(bedAssignService.confirmTrans(bdasTrnsSaveRequest)).build()
    }

    @Operation(summary = "입/퇴원/재택회송 처리", description = "")
    @POST
    @Path("confirmhosptlzdiscg")
    fun confirmhosptlzdiscg(@Valid bdasAdmsSaveRequest: BdasAdmsSaveRequest): Response {
        log.debug("PrivateBedAssignEndpoint confirmhosptlzdiscg >>> $bdasAdmsSaveRequest")
        return Response.ok(bedAssignService.confirmHosp(bdasAdmsSaveRequest)).build()
    }

    @Operation(summary = "병상배정 목록 (상태별)", description = "")
    @GET
    @Path("list")
    fun list(@BeanParam param: BdasListSearchParam): Response {
        return Response.ok(bedAssignService.findBedAsgnList(param)).build()
    }

    @GET
    @Path("remove/{ptId}/{step}")
    fun remove(@RestPath ptId: String?, @RestPath step: Int?): Response {
        return Response.ok(bedAssignService.removeData(ptId, step)).build()
    }
}