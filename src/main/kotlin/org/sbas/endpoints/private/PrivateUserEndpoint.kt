package org.sbas.endpoints.private

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import org.sbas.dtos.info.InfoCntcDto
import org.sbas.entities.info.InfoUser
import org.sbas.parameters.ModifyPwRequest
import org.sbas.parameters.ModifyTelnoRequest
import org.sbas.parameters.PageRequest
import org.sbas.services.UserService
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.BeanParam
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

    @Inject
    lateinit var userService: UserService

    @Operation(summary = "", description = "")
    @POST
    @Path("logout/{param}")
    fun logout(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "비밀번호 변경", description = "유저정보에서 비밀번호 변경 API")
    @POST
    @Path("modify-pw")
    fun modifyPw(@Valid modifyPwRequest: ModifyPwRequest): Response {
        return Response.ok(userService.modifyPw(modifyPwRequest)).build()
    }

    @Operation(summary = "핸드폰번호 변경", description = "유저정보에서 핸드폰번호 변경 API")
    @POST
    @Path("modify-telno")
    fun modifyTelno(@Valid modifyTelnoRequest: ModifyTelnoRequest): Response{
        return Response.ok(userService.modifyTelno(modifyTelnoRequest)).build()
    }

    @Operation(summary = "기본정보 수정", description = "유저정보에서 기본정보 수정 API")
    @POST
    @Path("modify-info")
    fun modifyInfo(@Valid infoUser: InfoUser) : Response {
        return Response.ok(userService.modifyInfo(infoUser)).build()
    }

    @Operation(summary = "사용자 초대", description = "")
    @POST
    @Path("invit")
    fun invit(): Response {
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

    @Operation(summary = "사용자 목록", description = "나와 관련된(연락처) - 즐겨찾기, 내조직, 알수도 있는사람 목록")
    @GET
    @Path("myusers")
    fun getMyUsers(): Response {
        return Response.ok(userService.getMyUsers()).build()
    }

    @Operation(summary = "사용자 상세")
    @GET
    @Path("user/{mbrId}")
    fun getMyUserDetail(@RestPath mbrId: String): Response{
        return Response.ok(userService.getMyUserDetail(mbrId)).build()
    }

    @Operation(summary = "전국 사용자 검색 목록", description = "전국 사용자 검색 목록 API(filter X)")
    @GET
    @Path("all-users")
    fun getAllUsers(@BeanParam pageRequest: PageRequest): Response {
        return Response.ok(userService.getAllUsers(pageRequest)).build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("search")
    fun search(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "즐겨찾기 등록", description = "내 연락처에 즐겨찾기로 등록 API")
    @POST
    @Path("reg-favorite")
    fun regFavorite(@Valid request: InfoCntcDto): Response {
        return Response.ok(userService.regFavorite(request)).build()
    }
}