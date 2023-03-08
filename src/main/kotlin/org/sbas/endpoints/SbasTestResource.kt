package org.sbas.endpoints

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.sbas.parameters.BaseCodeRequest
import org.sbas.response.BaseCodeResponse
import org.sbas.services.UserService
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext

@Tag(name = "테스트", description = "테스트 API")
@Path("v1")
class SbasTestResource {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var serv1: UserService

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
    @RolesAllowed("USER","ADMIN")
    fun test2(param1: BaseCodeRequest, @Context ctx: SecurityContext): BaseCodeResponse {
        val ret = serv1.getBaseCode(param1, ctx)
        return ret
    }

}