package org.sbas.endpoints.public

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestPath
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "대시보드(공개권한용)", description = "인증이 필요 없거나 비로그인 인증 사용자에 오픈되는 상황판")
@Path("v1/public")
class PublicDashboardEndpoint {

    @Operation(summary = "", description = "")
    @GET
    @Path("dashbd/{param}")
    fun dashbd(@RestPath param: String): Response {
        return Response.ok().build()
    }
}