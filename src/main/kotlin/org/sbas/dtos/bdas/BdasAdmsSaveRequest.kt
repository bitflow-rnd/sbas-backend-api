package org.sbas.dtos.bdas

import org.sbas.constants.enums.AdmsStatCd
import org.sbas.entities.bdas.BdasAdms
import org.sbas.entities.bdas.BdasAdmsId
import org.sbas.utils.StringUtils
import org.sbas.utils.annotation.ValidEnum
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

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
) {

    fun toEntity(admsStatCd: String, admsSeq: Int): BdasAdms {
        return when (admsStatCd) {
            AdmsStatCd.IOST0001.name -> toAdmsEntity(admsSeq)
            AdmsStatCd.IOST0002.name -> toDschEntity(admsSeq)
            AdmsStatCd.IOST0003.name -> toHomeEntity(admsSeq)
            else -> toAdmsEntity(admsSeq)
        }
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
