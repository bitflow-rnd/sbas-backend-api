package org.sbas.endpoints.admin

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "E-Gen 연동 관리(어드민 권한용)", description = "동기화, 등록, 수정, 삭제 등")
@Path("v1/admin/egen")
class AdminEgenEndpoint {

    @Inject
    lateinit var log: Logger

    @Operation(summary = "", description = "")
    @POST
    @Path("syncmedinsts")
    fun syncmedinsts(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("syncrealtmavailbeds")
    fun syncrealtmavailbeds(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("synccommcodes")
    fun synccommcodes(): Response {
        return Response.ok().build()
    }
}