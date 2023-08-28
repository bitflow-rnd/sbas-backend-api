package org.sbas.dtos.bdas

import java.time.Instant

data class BdasTimeLineDto(
    val title: String?,
    val by: String?, // instNm + ocpCdNm + userNm
    val updtDttm: Instant?,
    val msg: String?,
    val timeLineStatus: String?,
    val hospId: String?,
    val chrgId: String?,
    val asgnReqSeq: Int?,
) {
    constructor(
        title: String?,
        timeLineStatus: String?,
    ) : this(title, null, null, null, timeLineStatus, null, null, null)

    constructor(
        title: String?,
        by: String?,
        timeLineStatus: String?,
        hospId: String?,
        chrgId: String?,
        asgnReqSeq: Int?,
    ) : this(title, by, null, null, timeLineStatus, hospId, chrgId, asgnReqSeq)

    constructor(
        title: String?,
        by: String?,
        updtDttm: Instant?,
        msg: String?,
        timeLineStatus: String?,
    ) : this(title, by, updtDttm, msg, timeLineStatus, null, null, null)

    constructor(
        title: String?,
        by: String?,
        updtDttm: Instant?,
        msg: String?,
        timeLineStatus: String?,
        hospId: String?,
        chrgId: String?,
    ) : this(title, by, updtDttm, msg, timeLineStatus, hospId, chrgId, null)
}

data class TimeLineDtoList(
    val ptId: String?,
    val bdasSeq: Int?,
    val count: Int?,
    val items: List<BdasTimeLineDto>?,
)