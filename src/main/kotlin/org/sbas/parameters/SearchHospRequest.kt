package org.sbas.parameters

import com.fasterxml.jackson.annotation.JsonProperty
import org.sbas.utils.NoArg

@NoArg
data class SearchHospRequest(
    var pageRequest: PageRequest?,
    var dutyDivNam: MutableList<String>,
    var dstrCd1: String?,
    var dstrCd2: String?,
    var hospId: String?
)