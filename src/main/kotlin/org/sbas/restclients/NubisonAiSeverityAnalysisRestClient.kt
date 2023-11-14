package org.sbas.restclients

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.sbas.restresponses.NubisonAiSeverityAnalysisResponse
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces


@RegisterRestClient
@Consumes("application/json")
@Produces("application/json")
interface NubisonAiSeverityAnalysisRestClient {

    @POST
    @Path("/seldon/inference-stag/knuh-v3/v2/models/infer")
    fun infer(body: String) : NubisonAiSeverityAnalysisResponse
}