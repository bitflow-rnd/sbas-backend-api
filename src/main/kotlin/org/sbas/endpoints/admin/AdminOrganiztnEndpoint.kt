package org.sbas.endpoints.admin

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "기관 관리(어드민 권한용)", description = "등록, 수정, 삭제 등")
@Path("v1/admin/organ")
class AdminOrganiztnEndpoint{

    @Inject
    lateinit var log: Logger

    @Operation(summary = "", description = "")
    @POST
    @Path("regfirestatn")
    fun regfirestatn(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("modfirestatn/{param}")
    fun modfirestatn(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("delfirestatn/{param}")
    fun delfirestatn(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("regfireman/{param}")
    fun regfireman(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("modfireman/{param}")
    fun modfireman(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("delfireman/{param}")
    fun delfireman(@RestPath param: String): Response {
        return Response.ok().build()
    }
}