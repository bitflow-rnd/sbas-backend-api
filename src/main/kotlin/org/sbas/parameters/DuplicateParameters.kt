package org.sbas.parameters

/**
 * 사용자 ID, 전화번호 중복 체크를 위한 Param
 */
data class DuplicateParameters(
    var userId: String = "",
    var telno: String = "",
)