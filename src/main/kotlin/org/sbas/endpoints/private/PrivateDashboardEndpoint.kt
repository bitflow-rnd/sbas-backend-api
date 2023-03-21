package org.sbas.endpoints.private

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder
import org.sbas.constants.SbasConst
import org.sbas.response.CommonResponse
import org.sbas.services.DashboardService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Tag(name = "대시보드 조회(사용자용)", description = "로그인 된 사용자(세부권한별 분기) - 개인별 커스텀 대시보드")
@Path("v1/private/dashbd")
class PrivateDashboardEndpoint {

    @Inject
    lateinit var dashboardService: DashboardService

    @Operation(summary = "", description = "")
    @GET
    @Path("pc")
    fun pc(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("mobile")
    fun mobile(): RestResponse<CommonResponse<MutableMap<String, Long>>>? {
        val res = dashboardService.count()
        return ResponseBuilder.ok(CommonResponse(SbasConst.ResCode.SUCCESS,
            null, res)).build()
    }
}