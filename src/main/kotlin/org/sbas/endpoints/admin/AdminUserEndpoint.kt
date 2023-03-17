package org.sbas.endpoints.admin

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.sbas.entities.info.InfoUser
import org.sbas.response.StringResponse
import org.sbas.services.UserService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Tag(name = "사용자 관리(어드민 권한용)", description = "System Admin 사용자 - 사용자 등록, 수정, 삭제 등")
@Path("v1/admin/user")
class AdminUserEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var service: UserService

    @Operation(summary = "회원가입", description = "백오피스에서 어드민(전산담당)이 처리하는 API")
    @POST
    @Path("reg")
    fun reg(@RequestBody infoUser: InfoUser): Response {
        return try {
            Response.ok(service.reg(infoUser)).build()
        }catch (e: Exception) {
            val res = StringResponse()
            res.code = "01"
            res.message = e.localizedMessage
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(res)
                    .type(MediaType.APPLICATION_JSON)
                    .build()
        }
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("users")
    fun users(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("invite")
    fun invite(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("del")
    fun del(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("initpw")
    fun initpw(): Response {
        return Response.ok().build()
    }
}