package org.sbas.dtos.bdas

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant

data class BdasTimeLineDto(
    var title: String?,
    var by: String?, // instNm + ocpCdNm + jobCdNm + userNm
    var updtDttm: Instant?,
    var msg: String?,
    @JsonIgnore var inhpAsgnYn: String?,
    @JsonIgnore var jobCd: String?,
    @JsonIgnore var ocpCd: String?,
    @JsonIgnore var assignInstNm: String?,
) {
    constructor(
        title: String?,
        assignInstNm: String?,
    ) : this(title, assignInstNm, null, null, null, null, null, null)
}

data class TimeLineDtoList(
    var count: Int?,
    var items: List<BdasTimeLineDto>?,
)