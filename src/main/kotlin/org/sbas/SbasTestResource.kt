package org.sbas

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.sbas.entities.BaseCode
import org.sbas.entities.BaseCodeId
import org.sbas.repositories.BaseCodeRepository
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path

@Tag(name = "테스트", description = "테스트 API")
@Path("v1")
class SbasTestResource {

    @Inject
    lateinit var repo1: BaseCodeRepository

    @Operation(summary = "회원 조회", description = "id를 이용하여 user 레코드를 조회합니다.")
    @GET
    @Path("test1")
    fun test1(): BaseCode? {
        val id = BaseCodeId()
        id.cdGrpId = "12345678"
        id.cdId = "23456789"
        val ret = repo1.findById(id)
        return ret
    }

    @GET
    @Path("test2")
    @RolesAllowed("USER","ADMIN")
    fun test2(): BaseCode? {
        val id = BaseCodeId()
        id.cdGrpId = "12345678"
        id.cdId = "23456789"
        val ret = repo1.findById(id)
        return ret
    }

}