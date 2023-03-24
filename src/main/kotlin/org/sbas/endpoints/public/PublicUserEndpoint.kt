package org.sbas.endpoints.public

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.sbas.constants.SbasConst
import org.sbas.entities.info.InfoUser
import org.sbas.parameters.DuplicateParameters
import org.sbas.parameters.SmsSendRequest
import org.sbas.responses.CommonResponse
import org.sbas.responses.StringResponse
import org.sbas.services.UserService
import javax.annotation.security.PermitAll
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext

@Tag(name = "사용자 관리(공개 권한용)", description = "비 로그인 사용자 - 가입요청, 인증요청 등")
@Path("v1/public/user")
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
    fun existid(param: DuplicateParameters): CommonResponse<*> {
        val (res, message) = userService.checkUserId(param.userId)
        return CommonResponse(SbasConst.ResCode.SUCCESS, message, res)
    }

    @Operation(summary = "사용자 휴대폰번호 중복 조회", description = "")
    @POST
    @Path("existcellp")
    fun existcellp(param: DuplicateParameters):  CommonResponse<*> {
        val (res, message) = userService.checkTelNo(param.telno)
        return CommonResponse(SbasConst.ResCode.SUCCESS, message, res)
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("smssend")
    @PermitAll
    fun smssend(@Valid smsSendRequest: SmsSendRequest): Response {
        return Response.ok(userService.sendIdentifySms(smsSendRequest)).build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("confirmsms")
    fun confirmsms(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("requserreg")
    fun reqUserReg(@Valid infoUser: InfoUser): Response {
        return try {
            Response.ok(userService.reqUserReg(infoUser)).build()
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
    @POST
    @Path("login")
    fun login(): Response {
       return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("findid")
    fun findid(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("findpw")
    fun findpw(): Response {
        return Response.ok().build()
    }

}