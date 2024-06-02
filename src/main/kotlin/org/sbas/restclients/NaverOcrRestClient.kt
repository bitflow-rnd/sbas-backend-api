package org.sbas.restclients

import jakarta.validation.Valid
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.sbas.restparameters.NaverOcrApiParams
import org.sbas.restresponses.NaverOcrApiResponse
import org.sbas.utils.headerFactory.NaverOcrHeaderFactory


/**
 * Naver Clova OCR API를 처리하는 클라이언트
 */
//@Path("custom/v1/21302")
@Path("custom/v1/27088") // template ocr 주소 사용
@RegisterClientHeaders(NaverOcrHeaderFactory::class)
@RegisterRestClient
interface NaverOcrRestClient {
    /**
     * 파일 인식 요청
     */
    @POST
//    @Path("8e453b0208e971dcab58a1d1aad960e58e9b8652dc362baa6a1897e94561bfc8/general")
    @Path("df321c80562f9ad7cc40b35b95ee7b49e1ddc6686dc7203e2245e442dae5ff93/infer") // template ocr 주소 사용
    fun recognize(@Valid param: NaverOcrApiParams): NaverOcrApiResponse

}