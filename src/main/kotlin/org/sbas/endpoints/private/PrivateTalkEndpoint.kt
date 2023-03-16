package org.sbas.endpoints.private

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestPath
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "", description = "")
@Path("v1/private/talk")
class PrivateTalkEndpoint {

    @Operation(summary = "", description = "")
    @GET
    @Path("mychnls")
    fun mychnls(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("mychnl/{param}")
    fun mychnl(@RestPath param: String): Response {
        return Response.ok().build()
    }
}