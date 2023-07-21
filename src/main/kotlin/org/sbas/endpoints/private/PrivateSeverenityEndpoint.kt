package org.sbas.endpoints.private

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestPath
import org.sbas.handlers.NubisonAiSeverenityAnalysisHandler
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path


@Tag(name = "", description = "")
@Path("v1/private/severenity")
class PrivateSeverenityEndpoint {

    @Inject
    lateinit var nubisonAiSeverenityAnalysisHandler: NubisonAiSeverenityAnalysisHandler

    @Operation(summary = "", description = "")
    @GET
    @Path("analysis/{pid}")
    fun severenityAnalysis(@RestPath pid: String) : String {
        val result = nubisonAiSeverenityAnalysisHandler.analyse(pid)
        return result
    }

}