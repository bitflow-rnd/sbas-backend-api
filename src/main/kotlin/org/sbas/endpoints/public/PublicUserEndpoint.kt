package org.sbas.endpoints.public

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.sbas.constants.SbasConst
import org.sbas.dtos.info.InfoUserSaveReq
import org.sbas.entities.info.InfoUser
import org.sbas.parameters.CheckCertNoRequest
import org.sbas.parameters.LoginRequest
import org.sbas.parameters.SmsSendRequest
import org.sbas.responses.CommonResponse
import org.sbas.services.UserService
import javax.annotation.security.PermitAll
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext

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
    fun findId(@Valid infoUser: InfoUser): Response {
        return Response.ok(userService.findId(infoUser)).build()
    }

}