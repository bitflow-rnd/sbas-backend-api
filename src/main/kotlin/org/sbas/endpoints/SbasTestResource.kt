package org.sbas.endpoints

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.sbas.entities.BaseCode
import org.sbas.entities.BaseCodeId
import org.sbas.parameters.BaseCodeRequest
import org.sbas.repositories.BaseCodeRepository
import org.sbas.response.BaseCodeResponse
import org.sbas.services.UserService
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path

@Tag(name = "테스트", description = "테스트 API")
@Path("v1")
class SbasTestResource {

    @Inject
    lateinit var serv1: UserService

    @Operation(summary = "회원 조회", description = "id를 이용하여 user 레코드를 조회합니다.")
    @GET
    @Path("test1")
    fun test1(): BaseCodeResponse {
        val param1 = BaseCodeId()
        param1.cdGrpId = "12345678"
        param1.cdId = "23456789"
        val ret = serv1.getBaseCode(param1)
        return ret
    }

    @Operation(summary = "회원 조회 Advanced", description = "test1에 JWT와 접근권한 설정 등을 추가한 더 복잡한 예제입니다")
    @GET
    @Path("test2")
    @RolesAllowed("USER","ADMIN")
    fun test2(param1: BaseCodeRequest): BaseCodeResponse {
        val param2 = BaseCodeId()
        param2.cdGrpId = param1!!.cdGrpId!!
        param2.cdId = param1!!.cdId!!
        val ret = serv1.getBaseCode(param2)
        return ret
    }

}