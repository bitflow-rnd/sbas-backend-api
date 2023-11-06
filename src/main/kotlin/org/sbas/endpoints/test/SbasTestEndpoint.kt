package org.sbas.endpoints.test

import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.sbas.handlers.GeocodingHandler
import org.sbas.restparameters.NaverGeocodingApiParams
import org.sbas.restresponses.NaverGeocodingApiResponse
import org.sbas.restresponses.NaverReverseGeocodingApiResponse
import org.sbas.services.FirebaseService
import javax.annotation.security.PermitAll
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.core.SecurityContext


@Tag(name = "테스트", description = "내맘대로 테스트")
@Path("v1/test")
class SbasTestEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var geoHandler: GeocodingHandler

    @Inject
    lateinit var security: SecurityContext

    @Inject
    lateinit var firebaseService: FirebaseService

//    @GET
//    @Path("user")
//    @RolesAllowed("USER", "ADMIN")
//    fun getUser(): Response {
//        return Response.ok(object {
//            val token: JsonWebToken = serv1.getUser()
//        }).build()
//    }

    @POST
    @Path("geocoding-test")
    @PermitAll
    fun geocodingTest(@Valid params: NaverGeocodingApiParams): NaverGeocodingApiResponse{
        // test
        return geoHandler.getGeocoding(params)
    }

    @GET
    @Path("reverse-geocoding-test")
    @PermitAll
    fun reverseGeocodingTest(@QueryParam("latitude")latitude: Float, @QueryParam("longitude")longitude: Float): NaverReverseGeocodingApiResponse {
        return geoHandler.getReverseGeocoding(latitude, longitude)
    }

    @GET
    @Path("firebase-test")
    fun firebaseTest() {
        val registrationTokens: List<String> = listOf(
            "ewtRdFsYS3WdxtMKBhdYcT:APA91bGbTkGl9IYjdhdpvK0zxI58vobXWrJDlU6U939MExYyc-Q1EUovecgYQnotRTCTJeR-t7CjTx5jEKqO7tAw5quijUXh8R9L5HJuLvEKTmsV6fLFHHcTlEmQFXAFACbp86PubPep",
            "cEhrwWmnTC6D-asdLGnio7:APA91bEAPMX_aadsfasdfasdfasdfXpETomYkRlV36AP07QmiFuxw9o5-HB8fqDIJVMxW1-xaqDspq9NhLfJZFWoij90HBrRtyX0AvAX995L_rU3oX_gv6gVlDURFVe0Hx0pDYc5ynUNO4CWdKISX6glsnxau"
        )
//        firebaseService.sendMessage("jiseongtak", "123123", "jiseongtak")
        firebaseService.sendMessageMultiDevice("test", "testbody", "jiseongtak")
    }

}