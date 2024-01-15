package org.sbas.dtos.bdas

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.sbas.constants.enums.*
import org.sbas.utils.annotation.NoArg
import java.time.Instant
import jakarta.ws.rs.QueryParam

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
    val ptTypeCd: String,
    var ptTypeCdNm: String?,
    val svrtTypeCd: String?,
    var svrtTypeCdNm: String?,
    val undrDsesCd: String?,
    var undrDsesCdNm: String?,
    val reqBedTypeCd: String?,
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

    private val reqBedTypeCdNm: String?
        get() = reqBedTypeCd?.let { ReqBedTypeCd.valueOf(reqBedTypeCd).cdNm }

    @get:JsonProperty("tagList")
    private val tagList: List<String>
        get() {
            val list = emptyList<String>().toMutableList()
            ptTypeCdNm?.let { list.addAll(it.split(";")) }
            svrtTypeCdNm?.let { list.add(it) }
            undrDsesCdNm?.let { list.addAll(it.split(";")) }

            return list
        }

    @get:JsonProperty("ptTypeCdNmTagList")
    private val ptTypeCdNmTagList: List<String>?
        get() {
            return ptTypeCdNm?.split(";")
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
    @field: QueryParam("bedStatCd") var bedStatCd: String?,
    @field: QueryParam("ptTypeCd") var ptTypeCd: String?,
    @field: QueryParam("svrtTypeCd") var svrtTypeCd: String?,
    @field: QueryParam("gndr") var gndr: String?,
    @field: QueryParam("fromAge") var fromAge: Int?,
    @field: QueryParam("toAge") var toAge: Int?,
    @field: QueryParam("reqBedTypeCd") var reqBedTypeCd: String?,
    @field: QueryParam("page") var page: Int?,
)