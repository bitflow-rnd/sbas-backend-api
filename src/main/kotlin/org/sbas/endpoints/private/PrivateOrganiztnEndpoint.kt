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

@Tag(name = "기관 관리(사용자 권한용)", description = "로그인 된 사용자(세부권한별 분기) - 기관 등록 및 조회 등")
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
    @Path("medinstimg/{param}")
    fun medinstimg(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("regorganiztn/{param}")
    fun regorganiztn(@RestPath param: String): Response {
        return Response.ok().build()
    }

}