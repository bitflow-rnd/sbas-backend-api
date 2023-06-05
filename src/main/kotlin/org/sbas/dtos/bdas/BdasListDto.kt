package org.sbas.dtos.bdas

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant

data class BdasListDto(
    val ptId: String?,
    val bdasSeq: Int?,
    val ptNm: String?,
    val gndr: String?,
    val age: Int?,
    var bascAddr: String?,
    var updtDttm: Instant?,
    var diagNm: String?,
    var bedStatCd: String?,
    var bedStatCdNm: String?,
    var chrgInstNm: String?,
    @JsonIgnore var ptTypeCd: String?,
    @JsonIgnore var svrtTypeCd: String?,
    @JsonIgnore var undrDsesCd: String?
) {
    var tagList: MutableList<String>? = mutableListOf()
}