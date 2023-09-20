package org.sbas.endpoints.admin

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import org.sbas.dtos.BaseCodeGrpSaveReq
import org.sbas.dtos.BaseCodeGrpUpdateReq
import org.sbas.dtos.BaseCodeSaveReq
import org.sbas.dtos.BaseCodeUpdateReq
import org.sbas.dtos.info.DelNoticeReq
import org.sbas.dtos.info.ModNoticeReq
import org.sbas.dtos.info.NoticeActiveReq
import org.sbas.dtos.info.RegNoticeReq
import org.sbas.services.CommonService
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "공통 관리(어드민 권한용)", description = "System Admin 사용자 - 코드 등록, 수정, 삭제 등")
//@RolesAllowed("ADMIN")
@Path("v1/admin/common")
class AdminCommonEndpoint {

    @Inject
    private lateinit var log: Logger

    @Inject
    private lateinit var commonService: CommonService

    @Operation(summary = "공통코드 그룹 등록", description = "공통코드 그룹 등록")
    @POST
    @Path("regcodegrps")
    fun regCodeGroups(@Valid saveReq: BaseCodeGrpSaveReq): Response {
        return Response.ok(commonService.saveBaseCodeGrp(saveReq)).build()
    }

    @Operation(summary = "공통코드 그룹 목록", description = "공통코드 그룹 목록")
    @GET
    @Path("codegrps")
    fun getCodeGroups(): Response {
        return Response.ok(commonService.findBaseCdGrpList()).build()
    }

    @Operation(summary = "공통코드 그룹 수정", description = "공통코드 그룹 수정")
    @POST
    @Path("modcodegrps")
    fun modifyCodeGroups(@Valid updateReq: BaseCodeGrpUpdateReq): Response {
        return Response.ok(commonService.updateBaseCdGrp(updateReq)).build()
    }

    @Operation(summary = "공통코드 그룹 삭제", description = "공통코드 그룹 삭제")
    @POST
    @Path("delcodegrps/{cdGrpId}")
    fun deleteCodeGroups(@RestPath cdGrpId: String): Response {
        return Response.ok(commonService.deleteBaseCdGrp(cdGrpId)).build()
    }

    @Operation(summary = "공통코드 등록", description = "공통코드 등록")
    @POST
    @Path("regcode")
    fun regCode(@Valid baseCodeSaveReq: BaseCodeSaveReq): Response {
        return Response.ok(commonService.saveBaseCode(baseCodeSaveReq)).build()
    }

    @Operation(summary = "곻통코드 수정", description = "공통코드 수정")
    @POST
    @Path("modcode")
    fun modifyCode(baseCodeUpdateReq: BaseCodeUpdateReq): Response {
        return Response.ok(commonService.updateBaseCode(baseCodeUpdateReq)).build()
    }

    @Operation(summary = "공통코드 삭제", description = "공통코드 삭제")
    @POST
    @Path("delcode/{cdId}")
    fun deleteCode(@RestPath cdId: String): Response {
        return Response.ok(commonService.deleteBaseCode(cdId)).build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("egencodegrps")
    fun getEGenCodeGroups(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("modegencodegrps/{param}")
    fun modifyEGenCodeGroups(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("delegencodegrps/{param}")
    fun deleteEGenCodeGroups(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("modegencode/{param}")
    fun modifyEGenCode(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("delegencode/{param}")
    fun deleteEGenCode(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "공지사항 등록", description = "공지사항 등록 API")
    @POST
    @Path("reg-notice")
    fun regNotice(reqNoticeReq: RegNoticeReq): Response{
        return Response.ok(commonService.regNotice(reqNoticeReq)).build()
    }

    @Operation(summary = "공지사항 수정", description = "공지사항 수정 API")
    @POST
    @Path("mod-notice")
    fun regNotice(modNoticeReq: ModNoticeReq): Response {
        return Response.ok(commonService.modNotice(modNoticeReq)).build()
    }

    @Operation(summary = "공지사항 삭제", description = "공지사항 삭제 API")
    @POST
    @Path("del-notice")
    fun delNotice(delNoticeReq: DelNoticeReq): Response {
        return Response.ok(commonService.delNotice(delNoticeReq)).build()
    }

    @Operation(summary = "공지사항 활성화/비활성화", description = "공지사항 활성화/비활성화 API")
    @POST
    @Path("notice/active")
    fun modNoticeIsActive(noticeActiveReq: NoticeActiveReq): Response{
        return Response.ok(commonService.modNoticeIsActive(noticeActiveReq)).build()
    }

}