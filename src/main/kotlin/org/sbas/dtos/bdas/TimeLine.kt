package org.sbas.dtos.bdas

import java.time.Instant

abstract class TimeLine

class ClosedTimeLine(
    val title: String?,
    val timeLineStatus: String?,
) : TimeLine()

class SuspendTimeLine(
    val title: String?,
    val by: String?,
    val timeLineStatus: String?,
    val chrgInstId: String?,
    val chrgInstNm: String?,
    val chrgUserId: String?,
) : TimeLine()

class BdasAprvSuspendTimeLine(
    val title: String?,
    val by: String?,
    val timeLineStatus: String?,
    val chrgInstId: String?,
    val chrgInstNm: String?,
    val chrgUserId: String?,
    val asgnReqSeq: Int?,
) : TimeLine()

class CompleteTimeLine(
    val title: String?,
    val by: String?,
    val updtDttm: Instant?,
    val msg: String?,
    val timeLineStatus: String?,
    val chrgInstId: String?,
    val chrgInstNm: String?,
    val chrgUserId: String?,
) : TimeLine()

class BdasAprvCompleteTimeLine(
    val title: String?,
    val by: String?,
    val updtDttm: Instant?,
    val msg: String?,
    val timeLineStatus: String?,
    val chrgInstId: String?,
    val chrgInstNm: String?,
    val chrgUserId: String?,
    val asgnReqSeq: Int?,
) : TimeLine()


data class TimeLineList(
    val ptId: String?,
    val bdasSeq: Int?,
    val count: Int?,
    val items: List<TimeLine>?,
)