package org.sbas.endpoints.public

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestPath
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response

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