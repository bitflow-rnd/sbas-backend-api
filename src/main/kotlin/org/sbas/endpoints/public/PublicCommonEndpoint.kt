package org.sbas.endpoints.public

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.constants.SbasConst
import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeEgen
import org.sbas.responses.CommonResponse
import org.sbas.services.CommonService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext

@Tag(name = "공통 조회(공개 권한용)", description = "비 로그인 사용자 - 코드조회 등")
@Path("v1/public/common")
class PublicCommonEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var security: SecurityContext

    @Inject
    lateinit var service1: CommonService

    @Operation(summary = "", description = "")
    @GET
    @Path("regcommcodesets/{param}")
    fun regcommcodesets(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("searchcommcodesets/{param}")
    fun searchcommcodesets(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "E-GEN 공통코드 목록", description = "특정 코드 그룹(대분류 코드)에 따른 하위 공통코드 목록 조회")
    @GET
    @Path("egencodes/{cmMid}")
    fun egencodes(@RestPath cmMid: String): CommonResponse<List<BaseCodeEgen>> {
        return CommonResponse(SbasConst.ResCode.SUCCESS, "success", service1.findEgenCodeList(cmMid))
    }

    @Operation(summary = "공통코드 목록", description = "특정 코드 그룹에 따른 하위 공통코드 목록 조회")
    @GET
    @Path("codes/{cdGrpId}")
    fun codes(@RestPath cdGrpId: String): CommonResponse<List<BaseCode>> {
        return CommonResponse(SbasConst.ResCode.SUCCESS, "success", service1.findBaseCodeList(cdGrpId))
    }

    @Operation(summary = "시/도 목록", description = "")
    @GET
    @Path("sidos")
    fun sidos(): CommonResponse<List<BaseCode>> {
        return CommonResponse(SbasConst.ResCode.SUCCESS, "success", service1.findSidos())
    }

    @Operation(summary = "시/군/구 목록", description = "")
    @GET
    @Path("guguns/{cdGrpId}")
    fun guguns(@RestPath cdGrpId: String): CommonResponse<List<BaseCode>> {
        return CommonResponse(SbasConst.ResCode.SUCCESS, "success", service1.findGuguns(cdGrpId))
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("download/{param1}/{param2}")
    fun download(@RestPath param1: String, @RestPath param2: String): Response {
       return Response.ok().build()
    }

    @Operation(summary = "파일 업로드", description = "파일 업로드 API(attcId 반환)")
    @POST
    @Path("upload")
    fun upload(@RestForm param1: String, @RestForm param2: FileUpload): Response {
        // Todo: JSON result에 파일 ID(attcId) 반환
        return Response.ok(service1.publicFileUpload(param1, param2)).build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("prvinfocollctagrees")
    fun prvinfocollctagrees(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("prvinfocollctagree")
    fun prvinfocollctagree(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("svcusgterms")
    fun svcusgterms(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("svcusgterm")
    fun svcusgterm(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("prvinfoprocpolyc")
    fun prvinfoprocpolyc(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("ancmts")
    fun ancmts(): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("ancmt")
    fun ancmt(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("opensslicenss")
    fun opensslicenss(@RestPath param: String): Response {
        return Response.ok().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("appver")
    fun appver(): Response {
        return Response.ok().build()
    }

}