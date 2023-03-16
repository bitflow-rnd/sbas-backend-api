package org.sbas.endpoints.public

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

@Tag(name = "공통", description = "비 로그인 사용자를 위한 정보조회, 가입요청 및 로그인 API")
@Path("v1/public/common")
class PublicCommonEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var security: SecurityContext

    @Operation(summary = "", description = "")
    @GET
    @Path("regcommcodesets/{param}")
    fun regcommcodesets(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("searchcommcodesets/{param}")
    fun searchcommcodesets(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("egencodes/{param}")
    fun egencodes(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("sidos")
    fun sidos(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("guguns/{param}")
    fun guguns(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("codes/{param}")
    fun codes(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("download/{param1}/{param2}")
    fun download(@RestPath param1: String, @RestPath param2: String): Response {
       return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("upload")
    fun upload(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("files/{param}")
    fun files(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("prvinfocollctagrees")
    fun prvinfocollctagrees(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("prvinfocollctagree")
    fun prvinfocollctagree(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("svcuseterms")
    fun svcuseterms(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("svcusgterm")
    fun svcusgterm(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("prvinfoprocpolyc")
    fun prvinfoprocpolyc(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("ancmts")
    fun ancmts(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("ancmt")
    fun ancmt(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("opensslicenss")
    fun opensslicenss(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("appver")
    fun appver(): Response {
        return Response.ok().build()
    }

}