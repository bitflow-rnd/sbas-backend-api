package org.sbas.endpoints.private

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestPath
import org.sbas.handlers.NubisonAiSeverityAnalysisHandler
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path


@Tag(name = "", description = "")
@Path("v1/private/severity")
class PrivateSeverityEndpoint {

    @Inject
    lateinit var nubisonAiSeverityAnalysisHandler: NubisonAiSeverityAnalysisHandler

    @Operation(summary = "Get severity analysis from inference.nubison.ai", description = "")
    @GET
    @Path("analysis/{pid}")
    fun severityAnalysis(@RestPath pid: String) : String {
        val result = nubisonAiSeverityAnalysisHandler.analyse(pid)
        return result
    }

}