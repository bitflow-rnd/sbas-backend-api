package org.sbas.handlers

import org.eclipse.microprofile.rest.client.inject.RestClient
import org.sbas.restclients.NaverGeocodingRestClient
import org.sbas.restparameters.NaverGeocodingApiParams
import org.sbas.restresponses.NaverGeocodingApiResponse
import org.sbas.restresponses.NaverReverseGeocodingApiResponse
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class GeocodingHandler {

    @RestClient
    lateinit var naverGeocodingRestClient: NaverGeocodingRestClient

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

//    fun getReverseGeocoding(): NaverReverseGeocodingApiResponse{
//        return naverGeocodingRestClient.getLatLonInfo(
//            request = "coordsToaddr",
////            coords =
//        )
//    }

}