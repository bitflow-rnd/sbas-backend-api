package org.sbas.dtos

import org.sbas.constants.PtTypeCd
import java.time.Instant

data class InfoUserDto(
    var userId: String,
    var dutyDstr1Cd: String?,
    var instTypeCd: String?,
    var instNm: String?,
    var userNm: String?,
    var jobCd: String?,
    var authCd: String?,
    var rgstDttm: Instant,
    var statClasNm: String?,
)

data class InfoUserSearchDto(
    var dstr1Cd: String?,
    var dstr2Cd: String?,
    var userNm: String?,
    var telno: String?,
    var instNm: String?,
    var instTypeCd: String?,
    var ptTypeCd: String?,
    var statClasNm: String?,
)