package org.sbas.restclients

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.jboss.resteasy.reactive.RestPath
import org.sbas.restparameters.NaverSmsMsgApiParams
import org.sbas.restresponses.NaverSmsMsgApiResponse
import javax.validation.Valid
import javax.ws.rs.POST
import javax.ws.rs.Path


/**
 * Naver Sens 메시지 API를 처리하는 클라이언트
 */
@Path("sms/v2/services")
@RegisterRestClient
interface NaverSensRestClient {

    /**
     * 메시지 발송 요청
     */
    @POST
    @Path("{serviceId}/messages")
    fun messages(@RestPath serviceId: String, @Valid param: NaverSmsMsgApiParams): NaverSmsMsgApiResponse

}