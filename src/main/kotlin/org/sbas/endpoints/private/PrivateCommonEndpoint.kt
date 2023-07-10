package org.sbas.endpoints.private

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.services.CommonService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "공통 관리(사용자 권한용)", description = "로그인 된 사용자(세부권한별 분기) - 다운로드, 업로드, 코드 및 환자 목록조회 등")
@Path("v1/private/common")
class PrivateCommonEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var commonService: CommonService

    @Operation(summary = "파일목록 (권한별 공개 파일)", description = "")
    @GET
    @Path("files/{attcGrpId}")
    fun files(@RestPath attcGrpId: String): Response {
        return Response.ok(commonService.findFiles(attcGrpId)).build()
    }

    @Operation(summary = "다운로드 (권한별 공개 파일)", description = "")
    @GET
    @Path("download/{param1}/{param2}")
    fun download(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "업로드 (권한별 공개 파일)", description = "private 파일 업로드 API")
    @POST
    @Path("upload")
    fun upload(@RestForm param1: String, @RestForm fileUpload: MutableList<FileUpload>): Response {
        return Response.ok(commonService.privateFileUpload(param1, fileUpload)).build()
    }

    @Operation(summary = "개인정보수집동의 동의", description = "")
    @POST
    @Path("prvinfocollctagree/{param}")
    fun prvinfocollctagree(@RestPath param: String): Response {
        return Response.ok().build()
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
}