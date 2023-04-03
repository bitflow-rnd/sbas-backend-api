package org.sbas.parameters

import org.sbas.utils.NoArg

@NoArg
data class SearchParameters(
    var gndr: String,
    var natiCd: String,
    var dstr1Cd: String,
    var dstr2Cd: String,
)