package org.sbas.response

data class CommonResponse<T>(
    val code: T,
    val message: String,
    val result: T
)