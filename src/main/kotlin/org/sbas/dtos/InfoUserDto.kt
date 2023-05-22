package org.sbas.dtos

import org.sbas.constants.StatClas
import org.sbas.utils.NoArg
import java.time.Instant
import javax.ws.rs.QueryParam

data class InfoUserDto(
    var userId: String,
    var dutyDstr1Cd: String?,
    var instTypeCd: String?,
    var instNm: String?,
    var userNm: String?,
    var jobCd: String?,
    var authCd: String?,
    var rgstDttm: Instant,
    var statClas: StatClas?,
    var statClasNm: String?,
)

@NoArg
data class InfoUserSearchParam(
    @field:QueryParam("dstr1Cd") var dstr1Cd: String?,
    @field:QueryParam("dstr2Cd") var dstr2Cd: String?,
    @field:QueryParam("userNm") var userNm: String?,
    @field:QueryParam("telno") var telno: String?,
    @field:QueryParam("instNm") var instNm: String?,
    @field:QueryParam("instTypeCd") var instTypeCd: String?,
    @field:QueryParam("ptTypeCd") var ptTypeCd: String?,
    @field:QueryParam("statClas") var statClas: StatClas?,
)