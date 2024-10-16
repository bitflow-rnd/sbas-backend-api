package org.sbas.dtos.bdas

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.sbas.constants.enums.AdmsStatCd
import org.sbas.entities.bdas.BdasAdms
import org.sbas.entities.bdas.BdasAdmsId
import org.sbas.entities.svrt.SvrtPt
import org.sbas.entities.svrt.SvrtPtId
import org.sbas.utils.StringUtils
import org.sbas.utils.annotation.ValidEnum

data class BdasAdmsSaveRequest(
  @field: NotBlank val ptId: String,
  @field: NotNull val bdasSeq: Int,
  @field: NotBlank val hospId: String,
  val deptNm: String?,
  val wardNm: String?,
  val roomNm: String?,
  val spclId: String?,
  val spclNm: String?,
  val chrgTelno: String?,
  val dschRsnCd: String?,
  val msg: String?,
  @field: [NotNull ValidEnum(enumClass = AdmsStatCd::class)]
  val admsStatCd: String,
  val pid: String?,
  val monStrtDt: String?,
  val monStrtTm: String?,
) {

  fun toEntity(admsStatCd: AdmsStatCd, admsSeq: Int): BdasAdms {
    return when (admsStatCd) {
      AdmsStatCd.IOST0001 -> toAdmsEntity(admsSeq)
      AdmsStatCd.IOST0002 -> toDschEntity(admsSeq)
      AdmsStatCd.IOST0003 -> toHomeEntity(admsSeq)
    }
  }

  fun toSvrtPtEntity(rgstSeq: Int): SvrtPt {
    return SvrtPt(
      id = SvrtPtId(
        ptId = ptId,
        bdasSeq = bdasSeq,
        hospId = hospId,
        rgstSeq = rgstSeq,
      ),
      pid = pid ?: ptId,
      monStrtDt = monStrtDt ?: StringUtils.getYyyyMmDd(),
      monStrtTm = monStrtTm ?: StringUtils.getHhMmSs(),
    )
  }

  private fun toAdmsEntity(admsSeq: Int): BdasAdms {
    return BdasAdms(
      id = BdasAdmsId(ptId = ptId, bdasSeq = bdasSeq, admsSeq),
      hospId = hospId,
      deptNm = deptNm,
      roomNm = roomNm,
      spclId = spclId,
      spclNm = spclNm,
      chrgTelno = chrgTelno,
      admsDt = StringUtils.getYyyyMmDd(),
      admsTm = StringUtils.getHhMmSs(),
      msg = msg,
      admsStatCd = AdmsStatCd.IOST0001.name,
    )
  }

  private fun toDschEntity(admsSeq: Int): BdasAdms {
    return BdasAdms(
      id = BdasAdmsId(ptId = ptId, bdasSeq = bdasSeq, admsSeq),
      hospId = hospId,
      dschDt = StringUtils.getYyyyMmDd(),
      dschTm = StringUtils.getHhMmSs(),
      dschRsnCd = dschRsnCd,
      msg = msg,
      admsStatCd = AdmsStatCd.IOST0002.name,
    )
  }

  private fun toHomeEntity(admsSeq: Int): BdasAdms {
    return BdasAdms(
      id = BdasAdmsId(ptId = ptId, bdasSeq = bdasSeq, admsSeq),
      hospId = hospId,
      msg = msg,
      admsStatCd = AdmsStatCd.IOST0003.name,
    )
  }
}
