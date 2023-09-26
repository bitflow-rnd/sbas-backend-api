package org.sbas.endpoints.admin

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import org.sbas.dtos.info.FireStatnSaveReq
import org.sbas.dtos.info.InfoCrewSaveReq
import org.sbas.dtos.info.InfoCrewUpdateReq
import org.sbas.dtos.info.InfoInstUpdateReq
import org.sbas.entities.info.InfoCrewId
import org.sbas.services.OrganiztnService
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "기관 관리(어드민 권한용)", description = "System Admin 사용자 - 기관 등록, 수정, 삭제 등")
//@RolesAllowed("USER")
@Path("v1/admin/organ")
class AdminOrganiztnEndpoint{

    @Inject
    lateinit var log: Logger

    @Inject
    private lateinit var organiztnService: OrganiztnService

    @Operation(summary = "구급대 등록", description = "구급대 등록 API")
    @POST
    @Path("regfirestatn")
    fun regFireStatn(fireStatnSaveReq: FireStatnSaveReq): Response {
        return Response.ok(organiztnService.regFireStatn(fireStatnSaveReq)).build()
    }

    @Operation(summary = "구급대 수정", description = "구급대 수정 API")
    @POST
    @Path("modfirestatn")
    fun modFireStatn(infoInstUpdateReq: InfoInstUpdateReq): Response {
        return Response.ok(organiztnService.updateFireStatn(infoInstUpdateReq)).build()
    }

    @Operation(summary = "구급대 삭제", description = "구급대 삭제 API")
    @POST
    @Path("delfirestatn/{instId}")
    fun delFireStatn(@RestPath instId: String): Response {
        return Response.ok(organiztnService.deleteFireStatn(instId)).build()
    }

    @Operation(summary = "구급대원 조회", description = "구급대원 상세 조회 API")
    @GET
    @Path("fireman/{instId}/{crewId}")
    fun getFireman(@RestPath instId: String, @RestPath crewId: String): Response {
        return Response.ok(organiztnService.findFireman(instId, crewId)).build()
    }

    @Operation(summary = "구급대원 등록", description = "구급대원 등록 API")
    @POST
    @Path("reg-fireman")
    fun regFireman(@Valid infoCrewSaveReq: InfoCrewSaveReq): Response {
        return Response.ok(organiztnService.regFireman(infoCrewSaveReq)).build()
    }

    @Operation(summary = "구급대원 수정", description = "구급대원 수정 API")
    @POST
    @Path("mod-fireman")
    fun modFireman(@Valid updateReq: InfoCrewUpdateReq): Response {
        return Response.ok(organiztnService.modFireman(updateReq)).build()
    }

    @Operation(summary = "구급대원 삭제", description = "구급대원 삭제 API")
    @POST
    @Path("del-fireman")
    fun delFireman(@Valid infoCrewId: InfoCrewId): Response {
        return Response.ok(organiztnService.delFireman(infoCrewId)).build()
    }
}