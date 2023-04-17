package org.sbas.restclients

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.sbas.restparameters.NaverOcrApiParams
import org.sbas.restresponses.NaverOcrApiResponse
import org.sbas.utils.headerFactory.NaverOcrHeaderFactory
import javax.validation.Valid
import javax.ws.rs.POST
import javax.ws.rs.Path


/**
 * Naver Clova OCR API를 처리하는 클라이언트
 */
//@Path("custom/v1/21302")
@Path("custom/v1/21959") // template ocr 주소 사용
@RegisterClientHeaders(NaverOcrHeaderFactory::class)
@RegisterRestClient
interface NaverOcrRestClient {

    /**
     * 파일 인식 요청
     */
    @POST
//    @Path("8e453b0208e971dcab58a1d1aad960e58e9b8652dc362baa6a1897e94561bfc8/general")
    @Path("f7652e3870798fb5530893a27d8c57203432063092b37cce8efbd8a59ce17fec/infer") // template ocr 주소 사용
    fun recognize(@Valid param: NaverOcrApiParams): NaverOcrApiResponse

}