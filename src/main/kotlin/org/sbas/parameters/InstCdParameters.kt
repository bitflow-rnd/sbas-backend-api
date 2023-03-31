package org.sbas.parameters

/**
 * 지역 구분(시/도, 시/군/구) 코드 Param
 */
data class InstCdParameters(
    var dstrCd1: String? = null,
    var dstrCd2: String? = null,
    var instTypeCd: String? = null,
)