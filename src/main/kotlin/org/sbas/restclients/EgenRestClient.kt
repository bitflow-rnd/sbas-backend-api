package org.sbas.restclients

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.sbas.restresponses.EgenCodeMastItem
import org.sbas.restresponses.EgenResponse
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType


/**
 * E-GEN API를 처리하는 클라이언트
 */
@Path("/B552657")
@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_XML)
@RegisterRestClient
interface EgenRestClient {

    @GET
    @Path("/CodeMast/info")
    fun getCodeMast(@QueryParam("serviceKey") serviceKey: String, @QueryParam("CM_MID") CM_MID: String): EgenResponse

}