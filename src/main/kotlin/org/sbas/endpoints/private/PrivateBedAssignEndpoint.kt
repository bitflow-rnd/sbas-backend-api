package org.sbas.endpoints.private

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "병상배정 업무처리(사용자 권한용)", description = "로그인 된 사용자(세부권한별 분기) - 병상배정현황 조회 및 처리 등")
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
    @Path("confirmtransf")
    fun confirmtransf(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("confirmhosptlzdiscg")
    fun confirmhosptlzdiscg(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("list")
    fun list(): Response {
        return Response.ok().build()
    }
}