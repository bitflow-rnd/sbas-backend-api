package org.sbas.endpoints.test

import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.sbas.entities.info.InfoUser
import org.sbas.handlers.GeocodingHandler
import org.sbas.parameters.BaseCodeRequest
import org.sbas.responses.BaseCodeResponse
import org.sbas.restparameters.NaverGeocodingApiParams
import org.sbas.restresponses.NaverGeocodingApiResponse
import org.sbas.services.TestUserService
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.*


@Tag(name = "테스트", description = "내맘대로 테스트")
@Path("v1/test")
class SbasTestEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var serv1: TestUserService

    @Inject
    lateinit var geoHandler: GeocodingHandler

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

    @GET
    @Path("user")
    @RolesAllowed("USER", "ADMIN")
    fun getUser(): Response {
        return Response.ok(object {
            val token: JsonWebToken = serv1.getUser()
        }).build()
    }

    @POST
    @Path("geocoding-test")
    @PermitAll
    fun geocodingTest(@Valid params: NaverGeocodingApiParams): NaverGeocodingApiResponse{
        return geoHandler.getGeocoding(params)
    }
}