package org.sbas.parameters

import org.sbas.utils.NoArg

/**
 * 지역 구분(시/도, 시/군/구) 코드 Param
 */
@NoArg
data class DstrCdParameters(
    var dstrCd1: String,
    var dstrCd2: String,
)