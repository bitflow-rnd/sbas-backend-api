package org.sbas.dtos.bdas

import org.sbas.entities.bdas.BdasAprv
import org.sbas.entities.bdas.BdasAprvId
import org.sbas.utils.StringUtils
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class BdasAprvSaveRequest(
    @field: NotBlank
    val ptId: String,
    @field: NotNull
    val bdasSeq: Int,
    @field: NotNull
    val asgnReqSeq: Int,
    @field: NotBlank
    val hospId: String,
    @field: [NotBlank Pattern(regexp = "^[YN]\$", message = "Y/N 값만 가능합니다.")]
    val aprvYn: String,
    val negCd: String?,
    val msg: String?,
    val roomNm: String?,
    val deptNm: String?,
    val spclId: String?,
    val spclNm: String?,
    val chrgTelno: String?,
) {
    fun toRefuseEntity(msg: String?, negCd: String?): BdasAprv {
        return BdasAprv(
            id = BdasAprvId(
                ptId = this.ptId,
                bdasSeq = this.bdasSeq,
                asgnReqSeq = this.asgnReqSeq,
            ),
            hospId = this.hospId,
            aprvYn = this.aprvYn,
            negCd = negCd ?: this.negCd,
            msg = msg ?: this.msg,
        )
    }

    fun toApproveEntity(hospId: String?): BdasAprv {
        return BdasAprv(
            id = BdasAprvId(
                ptId = this.ptId,
                bdasSeq = this.bdasSeq,
                asgnReqSeq = this.asgnReqSeq,
            ),
            hospId = hospId ?: this.hospId,
            aprvDt = StringUtils.getYyyyMmDd(),
            aprvTm = StringUtils.getHhMmSs(),
            aprvYn = this.aprvYn,
            msg = this.msg,
            roomNm = this.roomNm,
            deptNm = this.deptNm,
            spclNm = this.spclNm,
            chrgTelno = this.chrgTelno,
        )
    }
}