package org.sbas.response

import org.eclipse.microprofile.openapi.annotations.media.Schema

abstract class AbstractResponse {

    @Schema(description = "응답코드", example = "100")
    lateinit var resCd: String

    @Schema(description = "에러메시지", example = "요청 파라미터가 부적절합니다")
    lateinit var message: String
}