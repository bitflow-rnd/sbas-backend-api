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
import javax.ws.rs.core.SecurityContext

@Tag(name = "사용자 관리(사용자 권한용)", description = "로그인 된 사용자(세부권한별 분기) - 로그아웃, 개인정보 수정, 알림메시지 조회 등")
@Path("v1/private/user")
class PrivateUserEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var security: SecurityContext

    @Operation(summary = "", description = "")
    @POST
    @Path("logout/{param}")
    fun logout(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("modbasic/{param}")
    fun modbasic(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("invit")
    fun invit(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("modpw")
    fun modpw(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("invitredir")
    fun invitredir(): Response {
        return Response.ok().build()
    }



    @Operation(summary = "", description = "")
    @POST
    @Path("confirmregreq/{param}")
    fun confirmregreq(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("modprofimg/{param}")
    fun modprofimg(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("modcellpno")
    fun modcellpno(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("noticnt")
    fun noticnt(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("notis")
    fun notis(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("myusers")
    fun myusers(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("user/{param}")
    fun user(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("search")
    fun search(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("regfavort/{param}")
    fun regfavort(@RestPath param: String): Response {
        return Response.ok().build()
    }
}