package org.sbas.endpoints.public

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.hibernate.validator.constraints.Length
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import org.sbas.constants.SbasConst
import org.sbas.parameters.InstCdParameters
import org.sbas.responses.CommonResponse
import org.sbas.services.OrganiztnService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response

@Tag(name = "기관 조회(공개 권한용)", description = "비 로그인 사용자 - 기관 정보 조회")
@Path("v1/public/organ")
class PublicOrganiztnEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var organiztnService: OrganiztnService

//    @Operation(summary = "의료기관 목록", description = "")
//    @GET
//    @Path("medinsts")
//    fun medinsts(): Response {
//        return Response.ok().build()
//    }
//
//    @Operation(summary = "의료기관 상세", description = "")
//    @GET
//    @Path("medinst/{param}")
//    fun medinst(@RestPath param: String): Response {
//        return Response.ok().build()
//    }

    @Operation(summary = "기관코드 목록", description = "기관코드 목록")
    @GET
    @Path("codes")
    fun getInstCodes(@QueryParam("dstrCd1") @Length(max = 2) dstrCd1: String?,
                     @QueryParam("dstrCd2") @Length(max = 4) dstrCd2: String?,
                     @QueryParam("instTypeCd") instTypeCd: String?): Response {
        return Response.ok(organiztnService.getInstCodes(dstrCd1, dstrCd2, instTypeCd)).build()
    }

    @Path("medinst/{param}")
    fun medinst(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "구급대 목록", description = "특정 지역 코드에 해당하는 구급대 목록")
    @GET
    @Path("firestatns")
    fun firestatns(param: InstCdParameters): CommonResponse<*> {
        //TODO 검색조건 추가(구급대명, 구급대 ID)
        val res = organiztnService.findFireStatns(param)
        return CommonResponse(SbasConst.ResCode.SUCCESS, "success", res)
    }

    @Operation(summary = "구급대 상세", description = "특정 구급대 소속의 구급대원 목록 및 차량번호 조회")
    @GET
    @Path("firestatn/{instId}")
    fun firestatn(@RestPath("instId") instId: String): CommonResponse<*> {
        val infoCrew = organiztnService.findInfoCrews(instId)
        return CommonResponse(SbasConst.ResCode.SUCCESS, "find crew", infoCrew)
    }

//    @Operation(summary = "", description = "")
//    @GET
//    @Path("organiztns/{param}")
//    fun organiztns(@RestPath param: String): Response {
//        return Response.ok().build()
//    }
}