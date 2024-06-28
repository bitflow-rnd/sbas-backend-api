package org.sbas.endpoints.prvt

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestPath
import org.sbas.handlers.NubisonAiSeverityAnalysisHandler
import org.sbas.responses.CommonResponse
import org.sbas.services.SvrtService


@Tag(name = "Managing severity", description = "Severity analysis")
@Path("v1/private/severity")
class PrivateSeverityEndpoint {

  @Inject
  lateinit var severityAnalysisHandler: NubisonAiSeverityAnalysisHandler

  @Inject
  lateinit var svrtService: SvrtService

  @Operation(summary = "Get severity analysis from inference.nubison.ai", description = "")
  @GET
  @Path("analysis/{pid}")
  fun severityAnalysis(@RestPath pid: String): Response {
    val result = severityAnalysisHandler.analyse(pid)
    return Response.ok(result).build()
  }

  @Operation(
    summary = "Get latest severity probs",
    description = "Get latest severity data (probabilities) for current patient by his ptId"
  )
  @GET
  @Path("probs")
  fun probs(@QueryParam("ptId") ptId: String): Response {
    val result: CommonResponse<*>?
    result = svrtService.getLastSvrtAnlyByPtId(ptId)
    return Response.ok(result).build()
  }

  @GET
  @Path("infos/{ptId}")
  fun severityInfo(@RestPath ptId: String): Response {
    return Response.ok(svrtService.findSeverityInfos(ptId)).build()
  }

}