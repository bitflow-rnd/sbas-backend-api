package org.sbas.dtos.bdas

import jakarta.ws.rs.QueryParam
import org.sbas.utils.annotation.NoArg

@NoArg
data class AvalHospListRequest(
  @field: QueryParam("dstr1Cd") val dstr1Cd: String?,
  @field: QueryParam("reqBedTypeCd") val reqBedTypeCd: String?,
  @field: QueryParam("svrtTypeCd") val svrtTypeCd: String?,
)
