package org.sbas.endpoints.public

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestPath
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response

@Tag(name = "대시보드 조회(공개 권한용)", description = "비 로그인 사용자 - 대시보드 조회")
@Path("v1/public")
class PublicDashboardEndpoint {

    @Operation(summary = "", description = "")
    @GET
    @Path("dashbd/{param}")
    fun dashbd(@RestPath param: String): Response {
        return Response.ok().build()
    }
}