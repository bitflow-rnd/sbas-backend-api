package org.sbas.dtos.bdas

import org.sbas.entities.info.UserActivityHistory

fun BdasReqSaveRequest.convertToActivityHistory(userId: String): UserActivityHistory {
    return UserActivityHistory(
        userId = userId,
        ptId = this.svrInfo.ptId,
        activityDetail = "병상요청",
    )
}

fun BdasReqAprvSaveRequest.convertToActivityHistory(userId: String): UserActivityHistory {
    return UserActivityHistory(
        userId = userId,
        ptId = this.ptId,
        activityDetail = if (this.aprvYn == "Y") "요청승인" else "배정불가"
    )
}

fun BdasAprvSaveRequest.convertToActivityHistory(userId: String): UserActivityHistory {
    return UserActivityHistory(
        userId = userId,
        ptId = this.ptId,
        activityDetail = if (this.aprvYn == "Y") "배정승인" else "배정불가"
    )
}

