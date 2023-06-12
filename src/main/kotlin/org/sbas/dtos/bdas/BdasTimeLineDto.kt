package org.sbas.dtos.bdas

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant

data class BdasTimeLineDto(
    val title: String?,
    val by: String?, // instNm + ocpCdNm + jobCdNm + userNm
    val updtDttm: Instant?,
    val msg: String?,
    val timeLineStatus: String?,
    @JsonIgnore var inhpAsgnYn: String?,
    @JsonIgnore var jobCd: String?,
    @JsonIgnore var ocpCd: String?,
    @JsonIgnore var assignInstNm: String?,
) {
    constructor(
        title: String?,
        assignInstNm: String?,
        timeLineStatus: String?,
    ) : this(title, assignInstNm, null, null, timeLineStatus, null, null, null, null)

    constructor(
        title: String?,
        by: String?,
        updtDttm: Instant?,
        msg: String?,
        timeLineStatus: String?,
    ) : this(title, by, updtDttm, msg, timeLineStatus, null, null, null, null)

    constructor(
        title: String?,
        timeLineStatus: String?,
    ) : this(title, null, null, null, timeLineStatus, null, null, null, null)
}

data class TimeLineDtoList(
    val count: Int?,
    val items: List<BdasTimeLineDto>?,
)