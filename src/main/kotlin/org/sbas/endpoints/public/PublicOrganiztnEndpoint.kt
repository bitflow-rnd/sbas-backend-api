package org.sbas.endpoints.public

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import org.sbas.constants.SbasConst
import org.sbas.response.CommonResponse
import org.sbas.services.OrganiztnService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response

@Tag(name = "기관 조회(공개 권한용)", description = "비 로그인 사용자 - 기관 정보 조회")
@Path("v1/public/organ")
class PublicOrganiztnEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var organiztnService: OrganiztnService

    @Operation(summary = "", description = "")
    @GET
    @Path("medinsts")
    fun medinsts(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("medinst/{param}")
    fun medinst(@RestPath param: String): Response {
        return Response.ok().build()
    }
    
    //TODO 특정 지역 조건 추가
    @Operation(summary = "구급대 목록", description = "특정 지역 코드에 해당하는 구급대 목록")
    @GET
    @Path("firestatns")
    fun firestatns(): CommonResponse<*> {
        val res = organiztnService.findInfoCrews()
        return CommonResponse(SbasConst.ResCode.SUCCESS, "success", res)
    }

    @Operation(summary = "구급대 상세", description = "특정 구급대 소속의 구급대원 목록 및 차량번호 조회")
    @GET
    @Path("firestatn/{instId}/{crewId}")
    fun firestatn(@PathParam("instId") instId: String, @PathParam("crewId") crewId: String): CommonResponse<*> {
        val infoCrew = organiztnService.findInfoCrewById(instId, crewId)
        return CommonResponse(SbasConst.ResCode.SUCCESS, "find crew", infoCrew)
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("organiztns/{param}")
    fun organiztns(@RestPath param: String): Response {
        return Response.ok().build()
    }
}