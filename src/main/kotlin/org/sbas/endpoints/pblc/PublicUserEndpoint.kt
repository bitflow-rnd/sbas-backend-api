package org.sbas.endpoints.pblc

import jakarta.annotation.security.PermitAll
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.sbas.constants.SbasConst
import org.sbas.dtos.info.InfoUserSaveReq
import org.sbas.parameters.*
import org.sbas.responses.CommonResponse
import org.sbas.services.UserService

@Tag(name = "사용자 관리(공개 권한용)", description = "비 로그인 사용자 - 가입요청, 인증요청 등")
@Path("v1/public/user")
@PermitAll
class PublicUserEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var security: SecurityContext

    @Inject
    lateinit var userService: UserService

    @Operation(summary = "사용자 아이디 중복 조회", description = "")
    @POST
    @Path("existid")
    fun existid(requestMap: Map<String, String>): CommonResponse<*> {
        val (res, message) = userService.checkUserId(requestMap["userId"])
        return CommonResponse(SbasConst.ResCode.SUCCESS, message, res)
    }

    @Operation(summary = "사용자 휴대폰번호 중복 조회", description = "")
    @POST
    @Path("existcellp")
    fun existcellp(telno: String):  CommonResponse<*> {
        val (res, message) = userService.checkTelNo(telno)
        return CommonResponse(SbasConst.ResCode.SUCCESS, message, res)
    }

    @Operation(summary = "sms 인증번호 전송", description = "sms 인증번호 전송 / 재전송")
    @POST
    @Path("smssend")
    @PermitAll
    fun smssend(@Valid smsSendRequest: SmsSendRequest): Response {
        return Response.ok(userService.sendIdentifySms(smsSendRequest)).build()
    }

    @Operation(summary = "sms 인증번호 확인", description = "sms 인증번호 확인 및 row 삭제")
    @POST
    @Path("confirmsms")
    fun checkCertNo(@Valid checkCertNoRequest: CheckCertNoRequest): Response {
        return Response.ok(userService.checkCertNo(checkCertNoRequest)).build()
    }

    @Operation(summary = "사용자 등록 요청", description = "사용자 조직 사용자 또는 어드민에게 요청 전송 API")
    @POST
    @Path("requserreg")
    fun reqUserReg(@Valid infoUserSaveReq: InfoUserSaveReq): Response {
        return Response.ok(userService.reqUserReg(infoUserSaveReq)).build()
    }

    @Operation(summary = "로그인", description = "로그인 API")
    @POST
    @Path("login")
    fun login(@Valid loginRequest: LoginRequest): Response {
       return Response.ok(userService.login(loginRequest)).build()
    }

    @Operation(summary = "아이디찾기", description = "아이디 찾기 API")
    @POST
    @Path("find-id")
    @PermitAll
    fun findId(@Valid findIdRequest: FindIdRequest): Response {
        return Response.ok(userService.findId(findIdRequest)).build()
    }

  @Operation(summary = "비밀번호 초기화", description = "비밀번호 초기화 API")
  @POST
  @Path("init-pw")
  @PermitAll
  fun initPw(@Valid initPwRequest: InitPwRequest): Response {
    return Response.ok(userService.initPw(initPwRequest)).build()
  }

  @Operation(summary = "비밀번호 재설정", description = "비밀번호 재설정 API")
  @POST
  @Path("find-pw")
  @PermitAll
  fun findPw(@Valid modifyPwRequest: ModifyPwRequest): Response {
    return Response.ok(userService.findPw(modifyPwRequest)).build()
  }

}