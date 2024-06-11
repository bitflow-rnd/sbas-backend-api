package org.sbas.endpoints.admn

import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.BeanParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.sbas.dtos.info.InfoUserSaveReq
import org.sbas.dtos.info.InfoUserSearchParam
import org.sbas.parameters.UpdatePushKeyRequest
import org.sbas.parameters.UserIdRequest
import org.sbas.parameters.UserRequest
import org.sbas.services.FirebaseService
import org.sbas.services.UserService

@Tag(name = "사용자 관리(어드민 권한용)", description = "System Admin 사용자 - 사용자 등록, 수정, 삭제 등")
@RolesAllowed("USER")
//@RolesAllowed("ADMIN")
@Path("v1/admin/user")
class AdminUserEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var firebaseService: FirebaseService

    @Operation(summary = "사용자등록 승인/반려", description = "백오피스에서 어드민(전산담당)이 처리하는 사용자 등록 승인/반려 API")
    @POST
    @Path("aprv")
    fun aprv(@Valid request: UserRequest): Response {
        return Response.ok(userService.aprv(request)).build()
    }

    @Operation(summary = "관리자 사용자 등록", description = "관리자 화면에서 사용자 등록")
    @POST
    @Path("reg")
    fun reg(@Valid infoUserSaveReq: InfoUserSaveReq): Response {
        return Response.ok(userService.reg(infoUserSaveReq)).build()
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
    fun updatePushKey(request: UpdatePushKeyRequest): Response? {
        return Response.ok(firebaseService.addPushKey(request.id, request.pushKey)).build()
    }

}