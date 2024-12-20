package org.sbas.endpoints.prvt

import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.BeanParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import org.sbas.dtos.info.InfoCntcDto
import org.sbas.dtos.info.InfoUserSearchFromUserParam
import org.sbas.dtos.info.InfoUserUpdateReq
import org.sbas.parameters.ModifyPwRequest
import org.sbas.parameters.ModifyTelnoRequest
import org.sbas.parameters.PageRequest
import org.sbas.services.UserService

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
    fun modifyInfo(@Valid infoUser: InfoUserUpdateReq) : Response {
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
    fun getUserDetail(@RestPath mbrId: String): Response{
        return Response.ok(userService.getMyUserDetail(mbrId)).build()
    }

    @Operation(summary = "전국 사용자 검색 목록", description = "전국 사용자 검색 목록 API(filter X)")
    @GET
    @Path("all-users")
    fun getAllUsers(@BeanParam pageRequest: PageRequest): Response {
        return Response.ok(userService.getAllUsers(pageRequest)).build()
    }

    @Operation(summary = "사용자 검색 목록", description = "사용자 검색 목록 API(filter O)")
    @GET
    @Path("users")
    fun search(@BeanParam request: InfoUserSearchFromUserParam): Response {
        return Response.ok(userService.getUsersFromUser(request)).build()
    }

    @Operation(summary = "즐겨찾기 등록", description = "내 연락처에 즐겨찾기 등록 API")
    @POST
    @Path("reg-favorite")
    fun regFavorite(@Valid request: InfoCntcDto): Response {
        return Response.ok(userService.regFavorite(request)).build()
    }

    @Operation(summary = "즐겨찾기 사용자 목록", description = "즐겨찾기 사용자 목록 API")
    @GET
    @Path("contact-users")
    fun getContactUsers(): Response {
        return Response.ok(userService.getContactUsers()).build()
    }

    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기 삭제 API")
    @POST
    @Path("del-favorite")
    fun delFavorite(@Valid request: InfoCntcDto): Response {
        return Response.ok(userService.delFavorite(request)).build()
    }

    @Operation(summary = "사용자 활동 내역 목록", description = "사용자 활동 내역 목록 API")
    @GET
    @Path("activity-history/{userId}")
    fun activityHistory(@RestPath userId: String): Response {
        return Response.ok(userService.getActivityHistory(userId)).build()
    }

  @Operation(summary = "알림 목록", description = "알림 목록 API")
  @GET
  @Path("alarm-list")
  fun alarmList(): Response {
    return Response.ok(userService.getAlarmList()).build()
  }

  @Operation(summary = "안읽은 알림 목록 갯수", description = "안읽은 알림 목록 갯수 조회 API")
  @GET
  @Path("alarm-count")
  fun alarmCount(): Response {
    return Response.ok(userService.getAlarmCount()).build()
  }

  @Operation(summary = "알림 읽기", description = "알림 읽음 처리 API")
  @POST
  @Path("read-alarms")
  fun readAlarms(): Response {
    return Response.ok(userService.readAlarms()).build()
  }
}