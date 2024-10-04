package org.sbas.endpoints.prvt

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestPath
import org.sbas.dtos.info.SvrtPtSearchParam
import org.sbas.responses.CommonResponse
import org.sbas.services.SvrtService

@Tag(name = "Managing severity", description = "Severity analysis")
@Path("v1/private/severity")
class PrivateSeverityEndpoint {

  @Inject
  lateinit var svrtService: SvrtService

  @Operation(
    summary = "Get latest severity probs",
    description = "Get latest severity data (probabilities) for current patient by his ptId"
  )
  @GET
  @Path("probs")
  fun probs(@QueryParam("ptId") ptId: String, @QueryParam("rgstSeq") rgstSeq: Int): Response {
    val result: CommonResponse<*>?
    result = svrtService.getLastSvrtAnly(ptId, rgstSeq)
    return Response.ok(result).build()
  }

  @Operation(
    summary = "중증 관찰 환자 정보 조회",
    description = "중증 관찰 환자의 생체 정보를 조회"
  )
  @GET
  @Path("infos/{ptId}")
  fun severityInfo(@RestPath ptId: String): Response {
    return Response.ok(svrtService.findSeverityInfos(ptId)).build()
  }

  @Operation(summary = "환자 중증정보 목록 조회", description = "")
  @GET
  @Path("list")
  fun svrtPtList(param: SvrtPtSearchParam): Response {
    return Response.ok(svrtService.findSvrtPtList(param)).build()
  }

}