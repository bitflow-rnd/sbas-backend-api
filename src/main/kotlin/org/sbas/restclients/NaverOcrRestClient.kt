package org.sbas.restclients

import org.eclipse.microprofile.config.Config
import org.eclipse.microprofile.config.ConfigProvider
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.sbas.restparameters.NaverClovaOcrApiParams
import org.sbas.restresponses.ClovaOcrApiResponse
import org.sbas.restresponses.NaverSmsMsgApiResponse
import javax.validation.Valid
import javax.ws.rs.POST
import javax.ws.rs.Path


/**
 * Naver Clova OCR API를 처리하는 클라이언트
 */
@Path("custom/v1/21302")
@ClientHeaderParam(name = "X-OCR-SECRET", value = ["{getAuthorizationHeader}"])
@RegisterRestClient
interface NaverOcrRestClient {

    /**
     * 파일 인식 요청
     */
    @POST
    @Path("8e453b0208e971dcab58a1d1aad960e58e9b8652dc362baa6a1897e94561bfc8/general")
    fun recognize(@Valid param: NaverClovaOcrApiParams): ClovaOcrApiResponse

    fun getAuthorizationHeader(): String? {
        val config: Config = ConfigProvider.getConfig()
        return config.getValue("restclient.naverocr.secret.key", String::class.java)
    }

}