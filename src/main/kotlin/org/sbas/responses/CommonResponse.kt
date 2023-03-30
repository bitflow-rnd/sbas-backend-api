package org.sbas.responses

import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.sbas.constants.SbasConst
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CommonResponse<T>(
    @NotNull
    @NotEmpty
    @Schema(description = "응답코드", example = SbasConst.ResCode.SUCCESS, maxLength = 2)
    val code: String,
    @Schema(description = "에러메시지", example = "사용권한이 없습니다")
    val message: String?,
    val result: T?
){
    constructor(result: T): this(SbasConst.ResCode.SUCCESS, "SUCCESS", result)

    constructor(message: String, result: T): this(SbasConst.ResCode.SUCCESS, message, result)

}