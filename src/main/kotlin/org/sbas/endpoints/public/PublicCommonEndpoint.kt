package org.sbas.endpoints.public

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.parameters.PageRequest
import org.sbas.services.CommonService
import org.sbas.services.FileService
import javax.annotation.security.PermitAll
import javax.inject.Inject
import javax.ws.rs.BeanParam
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

    @Inject
    lateinit var fileService: FileService

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
    fun egencodes(@RestPath cmMid: String): Response {
        return Response.ok(commonService.findCodeEgenList(cmMid)).build()
    }

    @Operation(summary = "공통코드 목록", description = "특정 코드 그룹에 따른 하위 공통코드 목록 조회")
    @GET
    @Path("codes/{cdGrpId}")
    fun codes(@RestPath cdGrpId: String): Response {
        return Response.ok(commonService.findBaseCodeList(cdGrpId)).build()
    }

    @Operation(summary = "시/도 목록", description = "시/도 목록 조회")
    @GET
    @Path("sidos")
    fun sidos(): Response {
        return Response.ok(commonService.findSidoList()).build()
    }

    @Operation(summary = "시/군/구 목록", description = "시/군/구 목록 조회")
    @GET
    @Path("guguns/{cdGrpId}")
    fun guguns(@RestPath cdGrpId: String): Response {
        return Response.ok(commonService.findGugunList(cdGrpId)).build()
    }

    @Operation(summary = "파일 다운로드 (전체 공개 파일)", description = "")
    @GET
    @Path("download/{attcGrpId}/{attcId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun download(@RestPath attcGrpId: String, @RestPath attcId: String): Response {
        return fileService.publicFileDownload(attcGrpId, attcId)
    }

    @Operation(summary = "파일 업로드 (전체 공개 파일)", description = "파일 업로드 API(attcId 반환)")
    @POST
    @Path("upload")
    fun upload(@RestForm param1: String?, @RestForm param2: MutableList<FileUpload>): Response {
        return Response.ok(fileService.publicFileUpload(param1, param2)).build()
    }

    @Operation(summary = "파일 읽기 (전체 공개 파일)", description = "attcId로 파일 읽기 API")
    @GET
    @Path("image/{attcId}")
    fun getImage(@RestPath attcId: String): Response {
        return Response.ok(fileService.getImage(attcId)).build()
    }

    @Operation(summary = "약관 목록", description = "약관 목록 API")
    @GET
    @Path("terms/{termsType}")
    fun getTermsByTermsType(@RestPath termsType: String): Response {
        return Response.ok(commonService.getTermsByTermsType(termsType)).build()
    }

    @Operation(summary = "약관 상세", description = "약관 상세 API")
    @GET
    @Path("terms/detail/{termsType}")
    fun getTermsDetailByTermsType(@RestPath termsType: String): Response {
        return Response.ok(commonService.getTermsDetailByTermsType(termsType)).build()
    }

    @Operation(summary = "공지사항 목록", description = "공지사항 목록 API")
    @GET
    @Path("notice")
    fun getNoticeList(@BeanParam pageRequest: PageRequest): Response {
        return Response.ok(commonService.getNoticeList(pageRequest)).build()
    }

    @Operation(summary = "공지사항 상세", description = "")
    @GET
    @Path("notice/{noticeId}")
    fun getNoticeDetail(@RestPath noticeId: String): Response {
        return Response.ok(commonService.getNoticeDetail(noticeId)).build()
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