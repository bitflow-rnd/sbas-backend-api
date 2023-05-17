package org.sbas.dtos.bdas

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant

data class BdasTimeLineDto(
    var userNm: String?,
    var jobCdNm: String?,
    var ocpCdNm: String?,
    var instNm: String?,
    var updtDttm: Instant?,
    @JsonIgnore var inhpAsgnYn: String?,
    var inhpAsgn: String?,
    var msg: String?,
    var assignInstNm: String?,
)