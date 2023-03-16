package org.sbas.endpoints.public

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.restclients.EgenRestClient
import org.sbas.restclients.NaverSensRestClient
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext

@Tag(name = "사용자 관리(공개 권한용)", description = "비 로그인 사용자 - 가입요청, 인증요청 등")
@Path("v1/public/user")
class PublicUserEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var security: SecurityContext

    @Operation(summary = "", description = "")
    @POST
    @Path("existid")
    fun existid(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("existcellphone")
    fun existcellphone(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("smssend")
    fun smssend(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("confirmsms")
    fun confirmsms(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("requserreg")
    fun requserreg(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("login")
    fun login(): Response {
       return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("findid")
    fun findid(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("findpw")
    fun findpw(): Response {
        return Response.ok().build()
    }
}