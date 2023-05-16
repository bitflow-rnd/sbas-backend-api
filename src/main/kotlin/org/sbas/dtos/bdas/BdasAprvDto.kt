package org.sbas.dtos.bdas

import org.sbas.entities.bdas.BdasAprv
import org.sbas.entities.bdas.BdasAprvId

data class BdasAprvDto(
    val ptId: String,
    val bdasSeq: Int,
    val aprvYn: String,
    val negCd: String?,
    val msg: String?,

    var hospId: String?,
    var roomNm: String?,
    var deptNm: String?,
    var spclNm: String?,
    var chrgTelno: String?,
) {
    fun toUnableAssignEntity(): BdasAprv {
        return BdasAprv(
            id = BdasAprvId(ptId = this.ptId, bdasSeq = this.bdasSeq),
            aprvYn = this.aprvYn,
            negCd = this.negCd,
            msg = this.msg,
            hospId = this.hospId,
            roomNm = this.roomNm,
            deptNm = this.deptNm,
            spclNm = this.spclNm,
            chrgTelno = this.chrgTelno,
        )
    }
}