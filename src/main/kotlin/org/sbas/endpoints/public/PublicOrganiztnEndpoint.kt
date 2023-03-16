package org.sbas.endpoints.public

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "기관조회 및 등록(공개권한용)", description = "인증이 필요없거나 게스트 인증 사용자에 오픈")
@Path("v1/public/organ")
class PublicOrganiztnEndpoint {

    @Inject
    lateinit var log: Logger

    @Operation(summary = "", description = "")
    @GET
    @Path("medinsts")
    fun medinsts(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("medinst/{param}")
    fun medinst(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("firestatns")
    fun firestatns(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("organiztns/{param}")
    fun organiztns(@RestPath param: String): Response {
        return Response.ok().build()
    }
}