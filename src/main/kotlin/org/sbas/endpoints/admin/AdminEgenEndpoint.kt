package org.sbas.endpoints.admin

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.sbas.restparameters.EgenApiBassInfoParams
import org.sbas.restparameters.EgenApiLcInfoParams
import org.sbas.restparameters.EgenApiListInfoParams
import org.sbas.services.EgenService
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Tag(name = "E-Gen 연동 관리(어드민 권한용)", description = "System Admin 사용자 - E-Gen API를 통한 데이터 동기화, 등록, 수정, 삭제 등")
@Path("v1/admin/egen")
class AdminEgenEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var egenService: EgenService

    @Operation(summary = "", description = "")
    @POST
    @Path("syncmedinsts")
    fun syncmedinsts(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("syncrealtmavailbeds")
    fun syncrealtmavailbeds(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("synccommcodes")
    fun synccommcodes(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "병‧의원 목록정보 조회", description = "병‧의원 목록정보 조회")
    @GET
    @Path("hsptlMdcncList")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun hsptlMdcncList(param: EgenApiListInfoParams): String {
        return egenService.getHsptlMdcncListInfoInqire(param).toString()
    }

    @Operation(summary = "병‧의원 위치정보 조회", description = "")
    @GET
    @Path("hsptlMdcncLcInfo")
    @Produces(MediaType.APPLICATION_JSON)
    fun hsptlMdcncLcInfo(param: EgenApiLcInfoParams): String {
        return egenService.getHsptlMdcncLcinfoInqire(param).toString()
    }

    @Operation(summary = "병의원 기본정보 조회", description = "")
    @GET
    @Path("hsptlBassInfo")
    @Produces(MediaType.APPLICATION_JSON)
    fun hsptlBassInfo(param: EgenApiBassInfoParams): String {
        return egenService.getHsptlBassInfoInqire(param).toString()
    }

}