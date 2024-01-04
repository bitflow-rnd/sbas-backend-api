package org.sbas.endpoints.public

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.hibernate.validator.constraints.Length
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import org.sbas.dtos.info.FireStatnSearchParam
import org.sbas.dtos.info.InfoHospSearchParam
import org.sbas.services.OrganiztnService
import jakarta.inject.Inject
import jakarta.ws.rs.BeanParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response

@Tag(name = "기관 조회(공개 권한용)", description = "비 로그인 사용자 - 기관 정보 조회")
@Path("v1/public/organ")
class PublicOrganiztnEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var organiztnService: OrganiztnService

    @Operation(summary = "의료기관 목록", description = "검색조건 및 거리 별 의료기관 조회. 페이징 및 총 검색 카운트도 반환")
    @GET
    @Path("medinsts")
    fun getMedInsts(@BeanParam param: InfoHospSearchParam): Response? {
        log.debug("PublicOrganiztnEndpoint getMedInsts >>> $param")
        return Response.ok(organiztnService.findInfoHospList(param)).build()
    }

    @Operation(summary = "의료기관 상세", description = "상세정보 및 해당 기관 등록 의료진 수 카운트 리턴")
    @GET
    @Path("medinst/{hpId}")
    fun getMedInst(@RestPath hpId: String): Response {
        return Response.ok(organiztnService.findInfoHospById(hpId)).build()
    }

    @Operation(summary = "기관코드 목록", description = "기관코드 목록")
    @GET
    @Path("codes")
    fun getInstCodes(@QueryParam("dstr1Cd") @Length(max = 2) dstr1Cd: String?,
                     @QueryParam("dstr2Cd") @Length(max = 4) dstr2Cd: String?,
                     @QueryParam("instTypeCd") instTypeCd: String?): Response {
        return Response.ok(organiztnService.getInstCodes(dstr1Cd, dstr2Cd, instTypeCd)).build()
    }

    @Operation(summary = "구급대 목록", description = "특정 지역 코드에 해당하는 구급대 목록")
    @GET
    @Path("firestatns")
    fun getFireStatns(@BeanParam param: FireStatnSearchParam): Response {
        log.debug("PublicOrganiztnEndpoint getFireStatns >>> $param")
        return Response.ok(organiztnService.findFireStatns(param)).build()
    }
}