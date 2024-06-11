package org.sbas.endpoints.prvt

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder
import org.sbas.constants.SbasConst
import org.sbas.responses.CommonResponse
import org.sbas.services.DashboardService

@Tag(name = "대시보드 조회(사용자용)", description = "로그인 된 사용자(세부권한별 분기) - 개인별 커스텀 대시보드")
@Path("v1/private/dashbd")
class PrivateDashboardEndpoint {

    @Inject
    lateinit var dashboardService: DashboardService

    @Operation(summary = "PC 대시보드", description = "")
    @GET
    @Path("pc")
    fun pc(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "모바일 홈 대시보드", description = "")
    @GET
    @Path("mobile")
    fun mobile(): RestResponse<CommonResponse<MutableMap<String, Long>>>? {
        val res = dashboardService.count()
        return ResponseBuilder.ok(CommonResponse(SbasConst.ResCode.SUCCESS,
            null, res)).build()
    }

    @Operation(summary = "중증도별 환자 집계")
    @GET
    @Path("severity")
    fun severity(): Response? {
        return Response.ok().build()
    }

    @Operation(summary = "지역별 중증환자 수용 집계")
    @GET
    @Path("severity/region")
    fun region(): Response? {
        return Response.ok().build()
    }
}