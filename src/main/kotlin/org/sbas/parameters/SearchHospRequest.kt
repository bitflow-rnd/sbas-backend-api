package org.sbas.parameters

import org.sbas.utils.annotation.NoArg

@NoArg
data class SearchHospRequest(
    var pageRequest: PageRequest?,
    var dutyDivNam: MutableList<String>,
    var dstr1Cd: String?,
    var dstr2Cd: String?,
    var hospId: String?
)