package org.sbas.response

import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.sbas.constants.SbasConst
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

abstract class AbstractResponse<T> {

    @NotNull
    @NotEmpty
    @Schema(description = "응답코드", example = SbasConst.ResCode.SUCCESS, maxLength = 2)
    var code: String? = SbasConst.ResCode.SUCCESS

    @Schema(description = "에러메시지", example = "사용권한이 없습니다")
    var message: String? = null

    abstract var result: T?

}