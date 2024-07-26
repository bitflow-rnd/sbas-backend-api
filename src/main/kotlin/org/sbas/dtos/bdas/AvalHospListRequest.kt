package org.sbas.dtos.bdas

import jakarta.ws.rs.QueryParam
import org.jboss.resteasy.reactive.Separator
import org.sbas.utils.annotation.NoArg

@NoArg
data class AvalHospListRequest(
  @field: QueryParam("dstr1Cd") var dstr1Cd: String?,
  @field: QueryParam("dstr2Cd") var dstr2Cd: String?,
  @field: QueryParam("dutyName") var dutyName: String?,
  @field: [QueryParam("reqBedTypeCd") Separator(",")] var reqBedTypeCd: List<String>?,
  @field: [QueryParam("svrtTypeCd") Separator(",")] var svrtTypeCd: List<String>?,
  @field: [QueryParam("ptTypeCd") Separator(",")] var ptTypeCd: List<String>?,
  @field: [QueryParam("equipment") Separator(",")] var equipment: List<String>?,
)
