package org.sbas.dtos.bdas

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.sbas.constants.enums.*
import org.sbas.utils.annotation.NoArg
import java.time.Instant
import javax.ws.rs.QueryParam

data class BdasListDto(
    val ptId: String,
    val bdasSeq: Int,
    val ptNm: String?,
    val gndr: String?,
    val age: Int?,
    val rrno1: String?,
    val mpno: String?,
    val bascAddr: String?,
    val updtDttm: Instant?,
    val diagNm: String?,
    val bedStatCd: String,
    var chrgInstNm: String?,
    val inhpAsgnYn: String?,
    var ptTypeCd: String,
    var svrtTypeCd: String?,
    var undrDsesCd: String?,
    @JsonIgnore
    val admsStatCd: String?,
) {
    @JsonProperty("bedStatCdNm")
    private val bedStatCdNm: String = bedStatCd.let {
        if (!admsStatCd.isNullOrEmpty()) {
            AdmsStatCd.valueOf(admsStatCd).cdNm
        } else {
            BedStatCd.valueOf(it).cdNm
        }
    }

    @JsonProperty("svrtTypeCdNm")
    private val svrtTypeCdNm = svrtTypeCd?.let { SvrtTypeCd.valueOf(it).cdNm }

    @JsonProperty("tagList")
    private val tagList: MutableList<String> = emptyList<String>().toMutableList()

    @JsonProperty("ptTypeCdNmTagList")
    private val ptTypeCdNmTagList: MutableList<String> = emptyList<String>().toMutableList()

    init {
        val splitPtTypeCd = ptTypeCd.split(";")
        val ptTypeCdNm = splitPtTypeCd.map { PtTypeCd.valueOf(it).cdNm }

        val splitUndrDsesCd = undrDsesCd?.split(";")
        val undrDsesCdNm = splitUndrDsesCd?.map { UndrDsesCd.valueOf(it).cdNm }

        tagList.addAll(ptTypeCdNm)
        tagList.add(svrtTypeCdNm!!)

        undrDsesCdNm?.let {
            tagList.addAll(it)
        }

        ptTypeCdNmTagList.addAll(ptTypeCdNm)
    }
}

data class BdasList(
    val title: String,
    val items: MutableList<BdasListDto>,
) {
    val count: Int
        get() = items.size
}

@NoArg
data class BdasListSearchParam(
    @field: QueryParam("ptNm") var ptNm: String?,
    @field: QueryParam("rrno1") var rrno1: String?,
    @field: QueryParam("mpno") var mpno: String?,
    @field: QueryParam("period") var period: Long?,
)