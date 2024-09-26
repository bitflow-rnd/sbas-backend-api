package org.sbas.endpoints.prvt

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.dtos.info.AgreeTermsListReq
import org.sbas.dtos.info.TermsAgreeReq
import org.sbas.services.CommonService
import org.sbas.services.FileService

@Tag(name = "공통 관리(사용자 권한용)", description = "로그인 된 사용자(세부권한별 분기) - 다운로드, 업로드, 코드 및 환자 목록조회 등")
@Path("v1/private/common")
class PrivateCommonEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var commonService: CommonService

    @Inject
    lateinit var fileService: FileService

    @Operation(summary = "파일목록 (권한별 공개 파일)", description = "")
    @GET
    @Path("files/{attcGrpId}")
    fun files(@RestPath attcGrpId: String): Response {
        return Response.ok(fileService.findFiles(attcGrpId)).build()
    }

    @Operation(summary = "다운로드 (권한별 공개 파일)", description = "")
    @GET
    @Path("download/{attcGrpId}/{attcId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun download(@RestPath attcGrpId: String, @RestPath attcId: String): Response {
        return fileService.publicFileDownload(attcGrpId, attcId)
    }

    @Operation(summary = "업로드 (권한별 공개 파일)", description = "private 파일 업로드 API")
    @POST
    @Path("upload")
    fun upload(@RestForm param1: String?, @RestForm param2: MutableList<FileUpload>?): Response {
        return Response.ok(fileService.privateFileUpload(param1, param2)).build()
    }

    @Operation(summary = "이미지 조회 (권한별 공개 파일)", description = "private 이미지 조회(바이트 스트림으로 반환)")
    @GET
    @Path("image/{attcId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun getPrivateImage(@RestPath attcId: String): Response? {
        val imageBytes = fileService.findPrivateImage(attcId)
        return Response.ok(imageBytes).build()
    }

    @Operation(summary = "서비스이용약관 동의", description = "")
    @POST
    @Path("svcusgterm/{param}")
    fun svcusgterm(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "모바일 설정 조회", description = "")
    @GET
    @Path("settings")
    fun settings(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "모바일 설정 수정", description = "")
    @POST
    @Path("modsettings")
    fun modsettings(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "약관 동의", description = "최신 버전 약관 동의 API")
    @POST
    @Path("terms/agree")
    fun termsAgree(termsAgreeReq: TermsAgreeReq): Response {
        return Response.ok(commonService.termsAgree(termsAgreeReq)).build()
    }

    @Operation(summary = "동의한 약관 목록", description = "동의한 약관 목록 API")
    @GET
    @Path("terms/agree-list")
    fun getAgreeTermsList(agreeTermsListReq: AgreeTermsListReq): Response {
        return Response.ok(commonService.getAgreeTermsList(agreeTermsListReq)).build()
    }

}