package org.sbas.dtos.info

import jakarta.validation.constraints.NotBlank
import jakarta.ws.rs.QueryParam
import org.hibernate.validator.constraints.Length
import org.sbas.entities.info.InfoNotice
import org.sbas.utils.annotation.NoArg

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

@NoArg
data class NoticeListReq(
    @field: QueryParam("userId") var userId: String,
    @field: QueryParam("page")var page: Int?,
    @field: QueryParam("size") var size: Int?,
    @field: QueryParam("isActive") var isActive: Boolean?,
    @field: QueryParam("searchPeriod") var searchPeriod: String?,
)