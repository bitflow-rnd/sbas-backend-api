package org.sbas.endpoints.private

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "", description = "")
@Path("v1/private/dashbd")
class PrivateDashboardEndpoint {

    @Operation(summary = "", description = "")
    @GET
    @Path("pc")
    fun pc(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("mobile")
    fun mobile(): Response {
        return Response.ok().build()
    }
}