package org.sbas.endpoints.public

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestPath
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "", description = "")
@Path("v1/public")
class PublicDashboardEndpoint {

    @Operation(summary = "", description = "")
    @GET
    @Path("dashbd/{param}")
    fun dashbd(@RestPath param: String): Response {
        return Response.ok().build()
    }
}