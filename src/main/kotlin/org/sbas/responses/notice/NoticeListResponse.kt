package org.sbas.responses.notice

data class NoticeListResponse(
    val noticeId: String,
    val title: String,
    val content: String,
    val noticeType: String,
    val startNoticeDt: String,
    val isRead: Boolean,
    val hasFile: Boolean,
)