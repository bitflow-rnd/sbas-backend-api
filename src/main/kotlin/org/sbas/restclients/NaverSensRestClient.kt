package org.sbas.restclients

import jakarta.validation.Valid
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.sbas.restdtos.NaverSmsMsgApiParams
import org.sbas.restdtos.response.NaverSmsMsgApiResponse
import org.sbas.utils.headerFactory.RequestNaverSmsHeaderFactory


/**
 * Naver Sens 메시지 API를 처리하는 클라이언트
 */
@Path("sms/v2/services")
@RegisterRestClient
@RegisterClientHeaders(RequestNaverSmsHeaderFactory::class)
interface NaverSensRestClient {

    /**
     * 메시지 발송 요청
     */
    @POST
    @Path("{serviceId}/messages")
    fun messages(@PathParam("serviceId") serviceId: String, @Valid param: NaverSmsMsgApiParams): NaverSmsMsgApiResponse

}