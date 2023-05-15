package org.sbas.dtos

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant

data class BdasListDto(
    var ptId: String?,
    var bdasSeq: Int?,
    var ptNm: String?,
    var gndr: String?,
    var age: Int?,
    var bascAddr: String?,
    var updtDttm: Instant?,
    var diagNm: String?,
    var bedStatCd: String?,
    var bedStatCdNm: String?,
    @JsonIgnore var ptTypeCd: String?,
    @JsonIgnore var svrtTypeCd: String?,
    @JsonIgnore var undrDsesCd: String?
) {
    var tagList: MutableList<String>? = mutableListOf()
}