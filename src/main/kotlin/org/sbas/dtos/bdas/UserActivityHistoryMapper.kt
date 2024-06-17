package org.sbas.dtos.bdas

import org.sbas.entities.info.UserActivityHistory

fun BdasReqSaveRequest.convertToActivityHistory(userId: String): UserActivityHistory {
    return UserActivityHistory(
        userId = userId,
        ptId = this.svrInfo.ptId,
        activityDetail = "BAST0003",
    )
}

fun BdasReqAprvSaveRequest.convertToActivityHistory(userId: String): UserActivityHistory {
    return UserActivityHistory(
        userId = userId,
        ptId = this.ptId,
        activityDetail = if (this.aprvYn == "Y") "BAST0004" else "BAST0008"
    )
}

fun BdasAprvSaveRequest.convertToActivityHistory(userId: String): UserActivityHistory {
    return UserActivityHistory(
        userId = userId,
        ptId = this.ptId,
        activityDetail = if (this.aprvYn == "Y") "BAST0005" else "BAST0008"
    )
}

fun BdasTrnsSaveRequest.convertToActivityHistory(userId: String): UserActivityHistory {
    return UserActivityHistory(
        userId = userId,
        ptId = this.ptId,
        activityDetail = "BAST0006",
    )
}

fun BdasAdmsSaveRequest.convertToActivityHistory(userId: String): UserActivityHistory {
    return UserActivityHistory(
        userId = userId,
        ptId = this.ptId,
        activityDetail = "BAST0007",
    )
}
