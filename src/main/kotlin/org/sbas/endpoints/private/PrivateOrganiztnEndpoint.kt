package org.sbas.endpoints.private

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.dtos.info.InfoCrewSearchParam
import org.sbas.dtos.info.InfoHospDetailDto
import org.sbas.dtos.info.toInfoHospDetail
import org.sbas.services.OrganiztnService
import jakarta.inject.Inject
import jakarta.ws.rs.BeanParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response

@Tag(name = "기관 관리(사용자 권한용)", description = "로그인 된 사용자(세부권한별 분기) - 기관 등록 및 조회 등")
@Path("v1/private/organ")
class PrivateOrganiztnEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    private lateinit var organiztnService: OrganiztnService

    @Operation(summary = "구급대 상세", description = "특정 구급대의 상세 정보 조회")
    @GET
    @Path("firestatn/{instId}")
    fun getFireStatn(@RestPath("instId") instId: String): Response {
        return Response.ok(organiztnService.findFireStatn(instId)).build()
    }

    @Operation(summary = "구급대원 목록", description = "특정 구급대 소속의 구급대원 목록 조회")
    @GET
    @Path("firemen")
    fun getFiremen(@BeanParam param: InfoCrewSearchParam): Response {
        return Response.ok(organiztnService.findFiremen(param)).build()
    }

    @Operation(summary = "의료기관 프로필 이미지 업로드 (신규/업데이트)", description = "")
    @POST
    @Path("medinstimg/{hospId}")
    fun medinstimg(@RestPath hospId: String, @RestForm fileUpload: FileUpload): Response {
        return Response.ok(organiztnService.uploadHospImg(hospId, fileUpload)).build()
    }

    @Operation(summary = "의료기관 프로필 이미지 삭제", description = "")
    @POST
    @Path("delete-medinstimg/{hospId}")
    fun deleteMedinstimg(@RestPath hospId: String): Response {
        return Response.ok(organiztnService.deleteHospImg(hospId)).build()
    }

    @Operation(summary = "의료기관 정보 조회(E-Gen 데이터 제외)")
    @GET
    @Path("medinstinfo/{hospId}")
    fun medInstInfo(@RestPath("hospId") hospId: String): Response {
        return Response.ok(organiztnService.findMedInstInfo(hospId)).build()
    }

    @Operation(summary = "의료기관 정보 수정(E-Gen 데이터 제외)")
    @POST
    @Path("mod-medinstinfo")
    fun modMedInstInfo(hospDetailRequest: InfoHospDetailDto): Response {
        val entity = hospDetailRequest.toInfoHospDetail()
        return Response.ok(organiztnService.modMedInstInfo(entity)).build()
    }

    @Operation(summary = "사용자 기관(조직) 등록", description = "")
    @POST
    @Path("regorganiztn/{param}")
    fun regorganiztn(@RestPath param: String): Response {
        return Response.ok().build()
    }

}