package org.sbas.endpoints.test

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.sbas.entities.info.InfoUser
import org.sbas.parameters.BaseCodeRequest
import org.sbas.response.BaseCodeResponse
import org.sbas.response.EgenCodeMastResponse
import org.sbas.services.TestUserService
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext

@Tag(name = "테스트", description = "테스트 API")
@Path("v1/test")
class SbasTestEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var serv1: TestUserService

    @Inject
    lateinit var security: SecurityContext

    @Operation(summary = "회원 조회", description = "id를 이용하여 user 레코드를 조회합니다.")
    @GET
    @Path("test1")
    fun test1(): BaseCodeResponse {
        val ret = serv1.getBaseCode()
        log.debug("api return value is $ret");
        return ret
    }

    @Operation(summary = "회원 조회 Advanced", description = "test1에 JWT와 접근권한 설정 등을 추가한 더 복잡한 예제입니다")
    @GET
    @Path("test2")
    @RolesAllowed("USER", "ADMIN")
    fun test2(param1: BaseCodeRequest, @Context ctx: SecurityContext): BaseCodeResponse {
        val user = security.userPrincipal
        var name = user.name
        val ret = serv1.getBaseCode(param1, ctx)
        return ret
    }

    @Operation(summary = "E-Gen Rest API 호출 테스트", description = "RESR Client 이용하여 E-GEN API를 조회합니다.")
    @GET
    @Path("test3")
    fun test3(): Response {
        var res: EgenCodeMastResponse?
        return try {
            res = serv1.getCodeMast()
            log.debug("api return value is $res");
            Response.ok(res).build()
        } catch (e: Exception) {
            res = EgenCodeMastResponse()
            res.code = "01"
            res.message = e.message
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(res)
                .type(MediaType.APPLICATION_JSON)
                .build()
        }
    }

    @Operation(summary = "E-Gen Rest API 호출 테스트", description = "RESR Client 이용하여 E-GEN API를 조회합니다.")
    @GET
    @Path("test4")
    fun test4(): Response {
        return try {
            val res = serv1.getHosptalMedclinic()
            log.debug("api return value is $res")
            Response.ok(res).build()
        } catch (e: Exception) {
            val res = EgenCodeMastResponse()
            res.code = "01"
            res.message = e.localizedMessage
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(res)
                .type(MediaType.APPLICATION_JSON)
                .build()
        }
    }
    @POST
    @Path("login")
    @PermitAll
    fun login(@RequestBody infoUser: InfoUser): String{
        return serv1.login(infoUser)
    }

}