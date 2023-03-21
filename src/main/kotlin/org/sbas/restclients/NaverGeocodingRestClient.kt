package org.sbas.restclients

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.locationtech.jts.geom.Coordinate
import org.sbas.restparameters.NaverGeocodingApiParams
import org.sbas.restparameters.NaverSmsMsgApiParams
import org.sbas.restresponses.NaverGeocodingApiResponse
import org.sbas.restresponses.NaverSmsMsgApiResponse
import org.sbas.utils.RequestNaverGeocodingHeaderFactory
import javax.validation.Valid
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam

/**
 * Naver Sens 메시지 API를 처리하는 클라이언트
 */
@Path("map-geocode/v2/geocode")
@RegisterRestClient
@RegisterClientHeaders(RequestNaverGeocodingHeaderFactory::class)
interface NaverGeocodingRestClient {

    /**
     * 주소 정보 요청
     */
    @GET
    fun getAddressInfo(@QueryParam("query") query: String,
                 @QueryParam("coordinate") coordinate: String?,
                 @QueryParam("filter") filter: String?,
                 @QueryParam("language") language: String?,
                 @QueryParam("page") page: Int?,
                 @QueryParam("count") count: Int?
                 ): NaverGeocodingApiResponse

}