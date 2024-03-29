package org.sbas.restclients

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.sbas.restresponses.NubisonAiSeverityAnalysisResponse
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces


@RegisterRestClient
@Consumes("application/json")
@Produces("application/json")
interface NubisonAiSeverityAnalysisRestClient {

    @POST
    @Path("/seldon/inference-stag/knuh-v4/v2/models/infer")
    fun infer(body: String) : NubisonAiSeverityAnalysisResponse
}