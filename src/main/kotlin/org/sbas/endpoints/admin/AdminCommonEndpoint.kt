package org.sbas.endpoints.admin

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder
import org.sbas.constants.SbasConst
import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeId
import org.sbas.responses.CommonResponse
import org.sbas.services.CommonService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "공통 관리(어드민 권한용)", description = "System Admin 사용자 - 코드 등록, 수정, 삭제 등")
@Path("v1/admin/common")
class AdminCommonEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    private lateinit var commonService: CommonService

    @Operation(summary = "공통코드 그룹 조회", description = "공통코드 그룹 조회")
    @GET
    @Path("codegrps")
    fun codegrps(): RestResponse<CommonResponse<List<BaseCode>>>? {
        return ResponseBuilder.ok(CommonResponse(
            SbasConst.ResCode.SUCCESS,
            null, commonService.findBaseCode())).build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("modcodegrps/{param}")
    fun modcodegrps(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "공통코드 그룹 삭제", description = "공통코드 그룹 삭제")
    @POST
    @Path("delcodegrps/{cdGrpId}/{cdId}")
    fun delcodegrps(@RestPath cdGrpId: String,
                    @RestPath cdId: String): Response {
        commonService.delCodeGrps(BaseCodeId(cdGrpId = cdGrpId, cdId = cdId))
        return Response.ok().build()
    }

    @Operation(summary = "공통코드 등록", description = "공통코드 등록")
    @POST
    @Path("regcode")
    fun regcode(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "곻통코드 수정", description = "")
    @POST
    @Path("modcode/{param}")
    fun modcode(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "공통코드 삭제", description = "")
    @POST
    @Path("delcode/{param}")
    fun delcode(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("egencodegrps")
    fun egencodegrps(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("modegencodegrps/{param}")
    fun modegencodegrps(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("delegencodegrps/{param}")
    fun delegencodegrps(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("modegencode/{param}")
    fun modegencode(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("delegencode/{param}")
    fun delegencode(@RestPath param: String): Response {
        return Response.ok().build()
    }
}