package org.sbas.dtos

import com.fasterxml.jackson.annotation.JsonIgnore
import org.sbas.constants.enums.*
import org.sbas.utils.annotation.NoArg
import java.time.Instant

data class SvrtInfoRsps(
  val ptId: String,
  val hospId: String,
  val anlyDt: String,
  val msreDt: String,
  val prdtDt: String?,
  val covSf: String,
  val oxygenApply: String,
)

@NoArg
data class SvrtPtSearchDto(
  val pid: String,
  val ptId: String?,
  val bdasSeq: Int?,
  val ptNm: String?,
  val gndr: String?,
  val rrno1: String?,
  val dstr1Cd: String?,
  val dstr1CdNm: String?,
  val dstr2Cd: String?,
  val dstr2CdNm: String?,
  val hospId: String?,
  val hospNm: String?,
  val mpno: String?,
  val natiCd: NatiCd?,
  val natiCdNm: String?,
  val updtDttm: Instant?,
  @JsonIgnore val ptTypeCd: String?,
  val svrtTypeCd: String?,
  @JsonIgnore val undrDsesCd: String?,
  val admsDt: String?,
  var covSf: CovSfRsps? = null,
) {
  val tagList: MutableList<String>
    get() {
      val tagList: MutableList<String> = mutableListOf()
      if (ptTypeCd != null) {
        val splitList = ptTypeCd.split(";")
        tagList.addAll(splitList.map { PtTypeCd.valueOf(it).cdNm })
      }
      if (svrtTypeCd != null) {
        val splitList = svrtTypeCd.split(";")
        tagList.addAll(splitList.map { SvrtTypeCd.valueOf(it).cdNm })
      }
      if (undrDsesCd != null) {
        val splitList = undrDsesCd.split(";")
        tagList.addAll(splitList.map { UndrDsesCd.valueOf(it).cdNm })
      }
      return tagList
    }

  val svrtTypeCdNm: String? = svrtTypeCd?.let { SvrtTypeCd.valueOf(it).cdNm }
}

data class CovSfRsps(
  val today: String?,
  val plusOneDay: String?,
  val plusTwoDay: String?,
  val plusThreeDay: String?,
)