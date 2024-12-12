package org.sbas.endpoints.prvt

import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestPath
import org.sbas.dtos.DsbdCardDetail
import org.sbas.services.DsbdService

@Tag(name = "대시보드 조회(사용자용)", description = "로그인 된 사용자(세부권한별 분기) - 개인별 커스텀 대시보드")
@Path("v1/private/dsbd")
class PrivateDashboardEndpoint(
  private val dsbdService: DsbdService,
) {

  @Operation(summary = "PC 대시보드", description = "")
  @GET
  @Path("pc")
  fun pc(): Response {
    return Response.ok().build()
  }

  @Operation(summary = "모바일 홈 대시보드", description = "")
  @GET
  @Path("mobile")
  fun mobile(): Response? {
    return Response.ok().build()
  }

  @POST
  fun saveDsbdItem(request: List<DsbdCardDetail>): Response {
    return Response.ok(dsbdService.saveDsbdItem(request)).build()
  }

  @Operation(summary = "병상 배정 상태별 현황")
  @GET
  @Path("bedStat/{period}")
  fun bedStat(@RestPath period: String): Response {
    return Response.ok(dsbdService.getBedStatData(period.toLong())).build()
  }

  @Operation(summary = "중증도별 현황")
  @GET
  @Path("severity/{period}")
  fun severity(@RestPath period: String): Response {
    return Response.ok(dsbdService.getSvrtData(period.toLong())).build()
  }

  @Operation(summary = "지역별 중증환자 수용 집계")
  @GET
  @Path("severity/region")
  fun region(): Response? {
    return Response.ok().build()
  }
}