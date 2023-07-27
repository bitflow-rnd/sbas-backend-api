package org.sbas.endpoints.private

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestPath
import org.sbas.handlers.NubisonAiSeverityAnalysisHandler
import org.sbas.services.SvrtService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response


@Tag(name = "Managing severity", description = "Severity analysis")
@Path("v1/private/severity")
class PrivateSeverityEndpoint {

    @Inject
    lateinit var nubisonAiSeverityAnalysisHandler: NubisonAiSeverityAnalysisHandler

    @Inject
    lateinit var svrtService: SvrtService

    @Operation(summary = "Get severity analysis from inference.nubison.ai", description = "")
    @GET
    @Path("analysis/{pid}")
    fun severityAnalysis(@RestPath pid: String): Response {
        val result = nubisonAiSeverityAnalysisHandler.analyse(pid)
        return Response.ok(result).build()
    }

    @Operation(
        summary = "Get latest severity probs",
        description = "Get latest severity data (probabilities) for current patient by his ptId"
    )
    @GET
    @Path("probs")
    fun probs(@QueryParam("ptId") ptId: String): Response {
        val result = svrtService.getLastSvrtAnlyByPtId(ptId)
        return Response.ok(result).build()
    }

}