package org.sbas.restclients

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.sbas.restdtos.NubisonAiSeverityAnalysisResponse


@RegisterRestClient
@Consumes("application/json")
@Produces("application/json")
interface NubisonAiSeverityAnalysisRestClient {

    @POST
    @Path("/seldon/inference-stag/knuh-v3/v2/models/infer")
    fun infer(body: String) : NubisonAiSeverityAnalysisResponse

    @POST
    @Path("/seldon/inference-stag/knuh-v4/v2/models/infer")
    fun inferV4(body: String) : NubisonAiSeverityAnalysisResponse

    @POST
    @Path("seldon/inference-stag/knuh-v5/v2/models/infer")
    fun inferV5(body: String) : NubisonAiSeverityAnalysisResponse
}