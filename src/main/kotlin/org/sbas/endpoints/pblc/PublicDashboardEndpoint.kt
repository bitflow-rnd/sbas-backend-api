package org.sbas.endpoints.pblc

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestPath
import org.sbas.services.DsbdService

@Tag(name = "대시보드 조회(공개 권한용)", description = "비 로그인 사용자 - 대시보드 조회")
@Path("v1/public/dsbd")
class PublicDashboardEndpoint(
  private val dsbdService: DsbdService,
) {

  @GET
  @Path("bedStat/{period}")
  fun bedStat(@RestPath period: String): Response {
    return Response.ok(dsbdService.getBedStatData(period.toLong())).build()
  }
}