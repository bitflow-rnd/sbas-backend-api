package org.sbas.endpoints.test

import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.sbas.handlers.GeocodingHandler
import org.sbas.restparameters.NaverGeocodingApiParams
import org.sbas.restresponses.NaverGeocodingApiResponse
import org.sbas.restresponses.NaverReverseGeocodingApiResponse
import org.sbas.services.TestUserService
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.core.*


@Tag(name = "테스트", description = "내맘대로 테스트")
@Path("v1/test")
class SbasTestEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var serv1: TestUserService

    @Inject
    lateinit var geoHandler: GeocodingHandler

    @Inject
    lateinit var security: SecurityContext

    @GET
    @Path("user")
    @RolesAllowed("USER", "ADMIN")
    fun getUser(): Response {
        return Response.ok(object {
            val token: JsonWebToken = serv1.getUser()
        }).build()
    }

    @POST
    @Path("geocoding-test")
    @PermitAll
    fun geocodingTest(@Valid params: NaverGeocodingApiParams): NaverGeocodingApiResponse{
        return geoHandler.getGeocoding(params)
    }

    @GET
    @Path("reverse-geocoding-test")
    @PermitAll
    fun reverseGeocodingTest(@QueryParam("latitude")latitude: Float, @QueryParam("longitude")longitude: Float): NaverReverseGeocodingApiResponse {
        return geoHandler.getReverseGeocoding(latitude, longitude)
    }

}