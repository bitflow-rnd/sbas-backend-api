package org.sbas.endpoints.pri

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "", description = "")
@Path("v1/private/bedasgn")
class PrivateBedAssignEndpoint {

    @Inject
    lateinit var log: Logger

    @Operation(summary = "", description = "")
    @POST
    @Path("reqconfirm")
    fun reqconfirm(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("asgnconfirm")
    fun asgnconfirm(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("transfproc")
    fun transfproc(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("hosptlzdischzproc")
    fun hosptlzdischzproc(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("bedasgns")
    fun bedasgns(): Response {
        return Response.ok().build()
    }
}