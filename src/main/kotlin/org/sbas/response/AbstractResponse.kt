package org.sbas.response

import org.eclipse.microprofile.openapi.annotations.media.Schema

abstract class AbstractResponse<T> {
    @Schema(description = "API 응답 코드", example = "00", maxLength = 2)
    var code: String? = "00"
    @Schema(description = "API 응답 메시지", example = "사용 권한이 없습니다")
    var message: String? = null
    abstract var result: T?
}