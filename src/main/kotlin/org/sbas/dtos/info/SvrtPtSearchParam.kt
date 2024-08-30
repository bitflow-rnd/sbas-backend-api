package org.sbas.dtos.info

import jakarta.ws.rs.QueryParam
import org.sbas.utils.annotation.NoArg

@NoArg
data class SvrtPtSearchParam(
  @field: QueryParam("dstr1Cd") var dstr1Cd: String?,
  @field: QueryParam("dstr2Cd") var dstr2Cd: String?,
  @field: QueryParam("hospNm") var hospNm: String?,
  @field: QueryParam("ptNm") var ptNm: String?,
  @field: QueryParam("rrno1") var rrno1: String?,
  @field: QueryParam("mpno") var mpno: String?,
  @field: QueryParam("ptId") var ptId: String?,
  @field: QueryParam("page") var page: Int?,
)
