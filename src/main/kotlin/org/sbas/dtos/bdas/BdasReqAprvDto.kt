package org.sbas.dtos.bdas

import com.fasterxml.jackson.annotation.JsonIgnore
import org.sbas.entities.bdas.BdasAprv
import org.sbas.entities.bdas.BdasAprvId
import org.sbas.entities.bdas.BdasReqAprv
import org.sbas.entities.bdas.BdasReqAprvId
import org.sbas.utils.NoArg
import org.sbas.utils.StringUtils

@NoArg
data class BdasReqAprvDto(
    val ptId: String,
    val bdasSeq: Int,
    val aprvYn: String,
    val negCd: String?,
    val msg: String?,
    var reqHospIdList: MutableList<String> = mutableListOf(),
) {
    fun toRefuseEntity(): BdasReqAprv {
        return BdasReqAprv(
            id = BdasReqAprvId(
                ptId = this.ptId,
                bdasSeq = this.bdasSeq,
                asgnReqSeq = 1,
            ),
            aprvYn = "N",
            negCd = this.negCd,
            msg = this.msg,
        )
    }

    fun toEntityWhenInHosp(): BdasReqAprv {
        return BdasReqAprv(
            id = BdasReqAprvId(
                ptId = this.ptId,
                bdasSeq = this.bdasSeq,
                asgnReqSeq = 1,
            ),
            aprvYn = "Y",
            msg = this.msg,
        )
    }

    fun toEntityWhenNotInHosp(asgnReqSeq: Int, hospId: String, hospNm: String): BdasReqAprv {
        return BdasReqAprv(
            id = BdasReqAprvId(
                ptId = this.ptId,
                bdasSeq = this.bdasSeq,
                asgnReqSeq = asgnReqSeq,
            ),
            aprvYn = "Y",
            msg = this.msg,
            reqHospId = hospId,
            reqHospNm = hospNm,
        )
    }
}

@NoArg
data class BdasAprvDto(
    val ptId: String,
    val bdasSeq: Int,
    val asgnReqSeq: Int,
    val hospId: String?,
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

    fun toApproveEntity(): BdasAprv {
        return BdasAprv(
            id = BdasAprvId(
                ptId = this.ptId,
                bdasSeq = this.bdasSeq,
                asgnReqSeq = this.asgnReqSeq,
            ),
            hospId = this.hospId,
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

data class BdasAprvResponse(
    val isAlreadyApproved: Boolean,
    val message: String?,
)

data class AvalHospListResponse(
    val hospId: String,
    val hospNm: String,
    @JsonIgnore
    val doubleDistance: Double,
    val distance: String?,
    val addr: String,
//    val tagList = mutableListOf<String>()
)