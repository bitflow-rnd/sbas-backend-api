package org.sbas.dtos.info

import java.time.Instant

data class UserActivityHistoryResponse(
    val id: Int,
    val userId: String,
    val ptId: String,
    val ptNm: String,
    val gndr: String,
    val age: Int,
    val dstr1Cd: String?,
    val dstr1CdNm: String?,
    val dstr2Cd: String?,
    val dstr2CdNm: String?,
    val activityDetail: String?,
    val rgstDttm: Instant,
)