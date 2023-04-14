package org.sbas.parameters

import org.sbas.utils.NoArg
import javax.ws.rs.QueryParam

@NoArg
data class SearchParameters(
    @field: QueryParam("gndr") var gndr: String,
    @field: QueryParam("natiCd") var natiCd: String,
    @field: QueryParam("dstr1Cd") var dstr1Cd: String,
    @field: QueryParam("dstr2Cd") var dstr2Cd: String,
)