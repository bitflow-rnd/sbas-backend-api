package org.sbas.endpoints.admin

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import org.sbas.dtos.BaseCodeSaveReq
import org.sbas.dtos.BaseCodeUpdateReq
import org.sbas.entities.base.BaseCode
import org.sbas.responses.CommonResponse
import org.sbas.services.CommonService
import javax.inject.Inject
import javax.validation.Valid
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

    @Operation(summary = "공통코드 그룹 등록", description = "공통코드 그룹 등록")
    @POST
    @Path("regcodegrp")
    fun regcodegrp(@Valid baseCodeSaveReq: BaseCodeSaveReq): CommonResponse<BaseCode> {
        return CommonResponse(commonService.saveBaseCodeGrp(baseCodeSaveReq))
    }

    @Operation(summary = "공통코드 그룹 목록", description = "공통코드 그룹 목록 조회")
    @GET
    @Path("codegrps")
    fun codegrps(): CommonResponse<List<BaseCode>> {
        return CommonResponse(commonService.findBaseCdGrpList())
    }

    @Operation(summary = "공통코드 그룹 수정", description = "공통코드 그룹 수정")
    @POST
    @Path("modcodegrps/{param}")
    fun modcodegrps(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "공통코드 그룹 삭제", description = "공통코드 그룹 삭제")
    @POST
    @Path("delcodegrps/{cdGrpId}")
    fun delcodegrps(@RestPath cdGrpId: String): CommonResponse<String> {
        return CommonResponse(commonService.deleteBaseCdGrp(cdGrpId))
    }

    @Operation(summary = "공통코드 등록", description = "공통코드 등록")
    @POST
    @Path("regcode")
    fun regcode(baseCodeSaveReq: BaseCodeSaveReq): CommonResponse<BaseCode> {
        return CommonResponse(commonService.saveBaseCode(baseCodeSaveReq))
    }

    @Operation(summary = "곻통코드 수정", description = "")
    @POST
    @Path("modcode")
    fun modcode(baseCodeUpdateReq: BaseCodeUpdateReq): CommonResponse<*> {
        return CommonResponse(commonService.updateBaseCode(baseCodeUpdateReq))
    }

    @Operation(summary = "공통코드 삭제", description = "")
    @POST
    @Path("delcode")
    fun delcode(baseCodeUpdateReq: BaseCodeUpdateReq): CommonResponse<*> {
        return CommonResponse(commonService.deleteBaseCode(baseCodeUpdateReq))
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