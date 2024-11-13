package org.sbas.handlers

import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.sbas.restclients.NaverGeocodingRestClient
import org.sbas.restdtos.NaverGeocodingApiParams
import org.sbas.restdtos.response.NaverGeocodingApiResponse
import org.sbas.restdtos.response.NaverReverseGeocodingApiResponse

@ApplicationScoped
class GeocodingHandler {

    @RestClient
    private lateinit var naverGeocodingRestClient: NaverGeocodingRestClient

    fun getGeocoding(naverGeocodingApiParams: NaverGeocodingApiParams): NaverGeocodingApiResponse {
        return naverGeocodingRestClient.getAddressInfo(
            naverGeocodingApiParams.query,
            naverGeocodingApiParams.coordinate,
            naverGeocodingApiParams.filter,
            naverGeocodingApiParams.language,
            naverGeocodingApiParams.page,
            naverGeocodingApiParams.count,
        )
    }

    fun getReverseGeocoding(latitude: Float, longitude: Float): NaverReverseGeocodingApiResponse {
        return naverGeocodingRestClient.getLatLonInfo(
            request = "coordsToaddr",
            coords = "$longitude,$latitude",
            sourcecrs = "epsg:4326",
            output = "json",
            orders = "addr,admcode,roadaddr",
        )
    }

}