package org.sbas.dtos.info

import org.hibernate.validator.constraints.Length
import org.sbas.entities.info.InfoNotice
import javax.validation.constraints.NotBlank

data class RegNoticeReq(
    @field: [NotBlank(message = "제목은 필수 값입니다.") Length(max = 30)]
    val title: String,
    val content: String,
    val isActive: Boolean?,
    val noticeType: String,
    val startNoticeDt: String,
    val startNoticeTm: String,
    val endNoticeDt: String,
    val endNoticeTm: String,
    val isUnlimited: Boolean?,
    val attcGrpId: String?,
) {
    fun toEntity(): InfoNotice {
        return InfoNotice(
            title = title,
            content = content,
            isActive = isActive ?: true,
            noticeType = noticeType,
            startNoticeDt = startNoticeDt,
            startNoticeTm = startNoticeTm,
            endNoticeDt = endNoticeDt,
            endNoticeTm = endNoticeTm,
            isUnlimited = isUnlimited ?: false,
            attcGrpId = attcGrpId
        )
    }
}

data class ModNoticeReq(
    @field: [NotBlank(message = "id는 필수 값입니다.") Length(max = 10)]
    val noticeId: String,
    val title: String,
    val content: String,
    val isActive: Boolean?,
    val noticeType: String,
    val startNoticeDt: String,
    val startNoticeTm: String,
    val endNoticeDt: String,
    val endNoticeTm: String,
    val isUnlimited: Boolean?,
    val attcGrpId: String?,
)

data class DelNoticeReq(
    @field: [NotBlank(message = "id는 필수 값입니다.") Length(max = 10)]
    val noticeId: String,
)

data class NoticeListReq(
    val userId: String,
    val page: Int?,
    val size: Int?,
    val isActive: Boolean?,
    val searchPeriod: String?,
)