package org.sbas.parameters

import com.fasterxml.jackson.annotation.JsonProperty
import org.sbas.utils.NoArg

/**
 * 지역 구분(시/도, 시/군/구) 코드 Param
 */
data class InstCdParameters(
    var dstrCd1: String? = null,
    var dstrCd2: String? = null,
    var instTypeCd: String? = null,
)