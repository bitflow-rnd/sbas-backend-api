package org.sbas.endpoints.admin

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.sbas.dtos.InfoUserSaveDto
import org.sbas.dtos.InfoUserSearchParam
import org.sbas.parameters.UpdatePushKeyRequest
import org.sbas.parameters.UserIdRequest
import org.sbas.parameters.UserRequest
import org.sbas.services.UserService
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.BeanParam
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "사용자 관리(어드민 권한용)", description = "System Admin 사용자 - 사용자 등록, 수정, 삭제 등")
@RolesAllowed("USER")
@Path("v1/admin/user")
class AdminUserEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var userService: UserService

    @Operation(summary = "사용자등록 승인/반려", description = "백오피스에서 어드민(전산담당)이 처리하는 사용자 등록 승인/반려 API")
    @POST
    @Path("reg")
    fun aprv(@Valid request: UserRequest): Response {
        return Response.ok(userService.aprv(request)).build()
    }

    @Operation(summary = "관리자 사용자 등록", description = "관리자 화면에서 사용자 등록")
    @POST
    @Path("aprv")
    fun reg(infoUserSaveDto: InfoUserSaveDto): Response {
        return Response.ok(userService.reg(infoUserSaveDto)).build()
    }

    @Operation(summary = "사용자 목록", description = "백오피스에서 어드민(전산담당)이 처리하는 사용자 목록 조회 API")
    @GET
    @Path("users")
    fun getUsers(@BeanParam param: InfoUserSearchParam): Response {
        return Response.ok(userService.getUsers(param)).build()
    }

    @Operation(summary = "등록자 초대 (관리자 모드)", description = "")
    @POST
    @Path("invite")
    fun invite(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "사용자 삭제", description = "백오피스에서 어드민(전산담당)이 처리하는 사용자 삭제 API")
    @POST
    @Path("del")
    fun deleteUser(@Valid request: UserIdRequest): Response {
        return Response.ok(userService.deleteUser(request)).build()
    }

    @Operation(summary = "push key 등록", description = "firebase push key 등록 API")
    @POST
    @Path("push-key")
    fun updatePushKey(request: UpdatePushKeyRequest): Response {
        return Response.ok(userService.updatePushKey(request)).build()
    }

}