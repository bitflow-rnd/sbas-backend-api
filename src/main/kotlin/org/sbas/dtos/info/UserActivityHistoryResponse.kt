package org.sbas.dtos.info

import java.time.Instant

data class UserActivityHistoryResponse(
    val userId: String,
    val ptId: String,
    val ptNm: String,
    val age: Int,
    val dstr1Cd: String?,
    val dstr1CdNm: String?,
    val dstr2Cd: String?,
    val dstr2CdNm: String?,
    val rgstDttm: Instant,
)