package org.sbas.endpoints.private

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "", description = "")
@Path("v1/public/organ")
class PrivateOrganiztnEndpoint {

    @Inject
    lateinit var log: Logger

    @Operation(summary = "", description = "")
    @GET
    @Path("firestatn/{param}")
    fun firestatn(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("regfirestatn")
    fun regfirestatn(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("medinstimg")
    fun medinstimg(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("regorganiztn/{param}")
    fun regorganiztn(@RestPath param: String): Response {
        return Response.ok().build()
    }
}