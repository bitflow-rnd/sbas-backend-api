package org.sbas.endpoints.pblc

import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestPath
import org.sbas.dtos.DsbdCardDetail
import org.sbas.responses.CommonResponse
import org.sbas.services.DsbdService

@Tag(name = "대시보드 조회(공개 권한용)", description = "비 로그인 사용자 - 대시보드 조회")
@Path("v1/public/dsbd")
class PublicDashboardEndpoint(
  private val dsbdService: DsbdService,
) {

  @GET
  @Path("bedStat/{period}")
  fun bedStat(@RestPath period: String): CommonResponse<List<DsbdCardDetail>> {
    return CommonResponse(dsbdService.getBedStatData(period.toLong()))
  }

  @POST
  @Path("dsbd/add")
  fun addItem() {

  }
}