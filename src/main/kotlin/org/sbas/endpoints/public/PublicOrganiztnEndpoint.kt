package org.sbas.endpoints.public

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.hibernate.validator.constraints.Length
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import org.sbas.dtos.info.FireStatnSearchParam
import org.sbas.dtos.info.InfoHospSearchParam
import org.sbas.services.OrganiztnService
import javax.inject.Inject
import javax.ws.rs.BeanParam
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
    fun getInstCodes(@QueryParam("dstrCd1") @Length(max = 2) dstrCd1: String?,
                     @QueryParam("dstrCd2") @Length(max = 4) dstrCd2: String?,
                     @QueryParam("instTypeCd") instTypeCd: String?): Response {
        return Response.ok(organiztnService.getInstCodes(dstrCd1, dstrCd2, instTypeCd)).build()
    }

    @Operation(summary = "구급대 목록", description = "특정 지역 코드에 해당하는 구급대 목록")
    @GET
    @Path("firestatns")
    fun getFireStatns(@BeanParam param: FireStatnSearchParam): Response {
        log.debug("PublicOrganiztnEndpoint getFireStatns >>> $param")
        return Response.ok(organiztnService.findFireStatns(param)).build()
    }
}