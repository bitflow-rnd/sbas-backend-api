package org.sbas.endpoints.admin

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.sbas.entities.info.InfoUser
import org.sbas.parameters.ModifyPwRequest
import org.sbas.parameters.ModifyTelnoRequest
import org.sbas.parameters.UserRequest
import org.sbas.services.UserService
import javax.annotation.security.PermitAll
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response

@Tag(name = "사용자 관리(어드민 권한용)", description = "System Admin 사용자 - 사용자 등록, 수정, 삭제 등")
@Path("v1/admin/user")
class AdminUserEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var userService: UserService

    @Operation(summary = "사용자등록 승인", description = "백오피스에서 어드민(전산담당)이 처리하는 사용자 등록 승인 API")
    @POST
//    @RolesAllowed("ADMIN")
    @Path("reg")
    fun reg(@Valid request: UserRequest): Response {
        return Response.ok(userService.reg(request)).build()
    }

    @Operation(summary = "사용자 목록", description = "백오피스에서 어드민(전산담당)이 처리하는 사용자 목록 API")
    @GET
//    @RolesAllowed("ADMIN")
    @Path("users")
    fun getUsers(@QueryParam("searchData") requestData: String): Response {
        return Response.ok(userService.getUsers(requestData)).build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("invite")
    fun invite(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "사용자 삭제", description = "백오피스에서 어드민(전산담당)이 처리하는 사용자 삭제 API")
    @POST
//    @RolesAllowed("ADMIN")
    @Path("del")
    fun deleteUser(@Valid request: UserRequest): Response {
        return Response.ok(userService.deleteUser(request)).build()
    }

    @Operation(summary = "비밀번호 변경", description = "")
    @POST
    @Path("modify-pw")
    fun modifyPw(@Valid modifyPwRequest: ModifyPwRequest): Response {
        return Response.ok(userService.modifyPw(modifyPwRequest)).build()
    }

    @Operation(summary = "전화번호 변경", description = "")
    @POST
    @Path("modify-telno")
    fun modifyTelNo(@Valid modifyTelnoRequest: ModifyTelnoRequest): Response{
        return Response.ok(userService.modifyTelno(modifyTelnoRequest)).build()
    }

    @Operation(summary = "기본정보 수정", description = "")
    @POST
    @Path("modify-info")
    fun modifyInfo(@Valid infoUser: InfoUser) : Response {
        return Response.ok(userService.modifyInfo(infoUser)).build()
    }

}