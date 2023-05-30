package org.sbas.endpoints.public

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeEgen
import org.sbas.responses.CommonResponse
import org.sbas.services.CommonService
import javax.annotation.security.PermitAll
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext


@Tag(name = "공통 조회(공개 권한용)", description = "비 로그인 사용자 - 코드조회 등")
@PermitAll
@Path("v1/public/common")
class PublicCommonEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var security: SecurityContext

    @Inject
    lateinit var commonService: CommonService

    @Operation(summary = "등록 관련 공통코드 셋 (일괄 조회)", description = "")
    @GET
    @Path("regcommcodesets/{param}")
    fun regcommcodesets(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "목록 검색조건 공통코드 셋 (일괄 조회)", description = "")
    @GET
    @Path("searchcommcodesets/{param}")
    fun searchcommcodesets(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "E-GEN 공통코드 목록", description = "특정 코드 그룹(대분류 코드)에 따른 하위 공통코드 목록 조회")
    @GET
    @Path("egencodes/{cmMid}")
    fun egencodes(@RestPath cmMid: String): CommonResponse<List<BaseCodeEgen>> {
        return CommonResponse(commonService.findCodeEgenList(cmMid))
    }

    @Operation(summary = "공통코드 목록", description = "특정 코드 그룹에 따른 하위 공통코드 목록 조회")
    @GET
    @Path("codes/{cdGrpId}")
    fun codes(@RestPath cdGrpId: String): CommonResponse<List<BaseCode>> {
        return CommonResponse(commonService.findBaseCodeList(cdGrpId))
    }

    @Operation(summary = "시/도 목록", description = "")
    @GET
    @Path("sidos")
    fun sidos(): CommonResponse<List<BaseCode>> {
        return CommonResponse(commonService.findSidoList())
    }

    @Operation(summary = "시/군/구 목록", description = "")
    @GET
    @Path("guguns/{cdGrpId}")
    fun guguns(@RestPath cdGrpId: String): CommonResponse<List<BaseCode>> {
        return CommonResponse(commonService.findGugunList(cdGrpId))
    }

    @Operation(summary = "파일 다운로드 (전체 공개 파일)", description = "")
    @GET
    @Path("download/{attcGrpId}/{attcId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun download(@RestPath attcGrpId: String, @RestPath attcId: String): Response {
        return commonService.publicFileDownload(attcGrpId, attcId)
    }

    @Operation(summary = "파일 업로드 (전체 공개 파일)", description = "파일 업로드 API(attcId 반환)")
    @POST
    @Path("upload")
    fun upload(@RestForm param1: String, @RestForm param2: FileUpload): Response {
        return Response.ok(commonService.publicFileUpload(param1, param2)).build()
    }

    @Operation(summary = "파일 읽기 (전체 공개 파일)", description = "attcId로 파일 읽기 API")
    @GET
    @Path("image/{attcId}")
    fun getImage(@RestPath attcId: String): Response {
        return Response.ok(commonService.getImage(attcId)).build()
    }

    @Operation(summary = "개인정보수집동의 목록", description = "")
    @GET
    @Path("prvinfocollctagrees")
    fun prvinfocollctagrees(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "개인정보수집동의 상세", description = "")
    @GET
    @Path("prvinfocollctagree")
    fun prvinfocollctagree(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "서비스이용약관 목록", description = "")
    @GET
    @Path("svcusgterms")
    fun svcusgterms(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "서비스이용약관 상세", description = "")
    @GET
    @Path("svcusgterm")
    fun svcusgterm(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "개인정보처리방침 상세", description = "")
    @GET
    @Path("prvinfoprocpolyc")
    fun prvinfoprocpolyc(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "공지사항 목록", description = "")
    @GET
    @Path("ancmts")
    fun ancmts(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "공지사항 상세", description = "")
    @GET
    @Path("ancmt")
    fun ancmt(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "오픈소스라이선스 목록", description = "")
    @GET
    @Path("opensslicenss")
    fun opensslicenss(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "앱 버전 조회", description = "")
    @GET
    @Path("appver")
    fun appver(): Response {
        return Response.ok().build()
    }

}