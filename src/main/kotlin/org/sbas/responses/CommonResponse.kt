package org.sbas.responses

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.sbas.constants.SbasConst

open class CommonResponse<T>(
    @NotNull
    @NotEmpty
    @Schema(description = "응답코드", example = SbasConst.ResCode.SUCCESS, maxLength = 2)
    val code: String,
    @Schema(description = "에러메시지", example = "사용권한이 없습니다")
    val message: String?,
    var result: T?
){
    constructor(result: T): this(SbasConst.ResCode.SUCCESS, "SUCCESS", result)

    constructor(message: String, result: T): this(SbasConst.ResCode.SUCCESS, message, result)

    constructor(): this(SbasConst.ResCode.SUCCESS, "SUCCESS", null)
}

class CommonListResponse<T>(
    @JsonIgnore
    val list: List<T>,
    @JsonIgnore
    val count: Int? = null,
) : CommonResponse<Result<T>>() {
    init {
        result = Result(
            items = list,
            count = count ?: list.size,
        )
    }
}

data class Result<T>(
    val count: Int,
    val items: List<T>
)