package org.sbas.dtos.info

import org.hibernate.validator.constraints.Length
import org.sbas.entities.info.InfoNotice
import javax.validation.constraints.NotBlank

data class RegNoticeReq(
    @field: [NotBlank(message = "제목은 필수 값입니다.") Length(max = 30)]
    val title: String,
    val content: String?,
    val isActive: Boolean?,
) {
    fun toEntity(): InfoNotice {
        return InfoNotice(
            title = title,
            content = content,
            isActive = isActive ?: true,
        )
    }
}