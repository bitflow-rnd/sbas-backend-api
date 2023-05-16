package org.sbas.restclients

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.sbas.restresponses.NaverGeocodingApiResponse
import org.sbas.restresponses.NaverReverseGeocodingApiResponse
import org.sbas.utils.headerFactory.RequestNaverGeocodingHeaderFactory
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam

/**
 * Naver Sens 메시지 API를 처리하는 클라이언트
 */
@RegisterRestClient
@RegisterClientHeaders(RequestNaverGeocodingHeaderFactory::class)
interface NaverGeocodingRestClient {

    /**
     * 주소 정보 요청
     */
    @GET
    @Path("map-geocode/v2/geocode")
    fun getAddressInfo(@QueryParam("query") query: String,
                 @QueryParam("coordinate") coordinate: String?,
                 @QueryParam("filter") filter: String?,
                 @QueryParam("language") language: String?,
                 @QueryParam("page") page: Int?,
                 @QueryParam("count") count: Int?
                 ): NaverGeocodingApiResponse

    @GET
    @Path("map-reversegeocode/v2/gc")
    fun getLatLonInfo(@QueryParam("request")request: String,
                      @QueryParam("coords")coords: String,
                      @QueryParam("sourcecrs")sourcecrs: String,
                      @QueryParam("output")output: String,
                      @QueryParam("orders")orders: String): NaverReverseGeocodingApiResponse

}