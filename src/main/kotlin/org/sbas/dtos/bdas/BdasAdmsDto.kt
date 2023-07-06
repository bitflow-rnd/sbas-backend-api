package org.sbas.dtos.bdas

import org.sbas.entities.bdas.BdasAdms
import org.sbas.entities.bdas.BdasAdmsId
import org.sbas.utils.NoArg
import org.sbas.utils.StringUtils

@NoArg
data class BdasAdmsSaveDto(
    val ptId: String,
    val bdasSeq: Int,
    val hospId: String?,
    val deptNm: String?,
    val wardNm: String?,
    val roomNm: String?,
    val spclId: String?,
    val spclNm: String?,
    val chrgTelno: String?,
    val dschRsnCd: String?,
    val msg: String?,
    val admsStatCd: String?,
) {
    fun toAdmsEntity(): BdasAdms {
        return BdasAdms(
            id = BdasAdmsId(ptId, bdasSeq),
            hospId = hospId,
            deptNm = deptNm,
            wardNm = wardNm,
            roomNm = roomNm,
            spclId = spclId,
            spclNm = spclNm,
            chrgTelno = chrgTelno,
            admsDt = StringUtils.getYyyyMmDd(),
            admsTm = StringUtils.getHhMmSs(),
            msg = msg,
            admsStatCd = "IOST0001",
        )
    }

    fun toDschEntity(): BdasAdms {
        return BdasAdms(
            id = BdasAdmsId(ptId, bdasSeq),
            hospId = hospId,
            dschDt = StringUtils.getYyyyMmDd(),
            dschTm = StringUtils.getHhMmSs(),
            dschRsnCd = dschRsnCd,
            msg = msg,
            admsStatCd = "IOST0002",
        )
    }

    fun toHomeEntity(): BdasAdms {
        return BdasAdms(
            id = BdasAdmsId(ptId, bdasSeq),
            hospId = hospId,
            msg = msg,
            admsStatCd = "IOST0003",
        )
    }
}
