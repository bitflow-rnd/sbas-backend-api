package org.sbas.dtos.bdas

import org.sbas.constants.AsgnStat
import org.sbas.entities.bdas.BdasAprv
import org.sbas.entities.bdas.BdasAprvId
import org.sbas.entities.bdas.BdasAsgnAprv
import org.sbas.entities.bdas.BdasAsgnAprvId
import org.sbas.utils.NoArg

@NoArg
data class BdasAsgnAprvDto(
    val ptId: String,
    val bdasSeq: Int,
    val aprvYn: String,
    val negCd: String?,
    val msg: String?,
    var reqHospIdList: MutableList<String> = mutableListOf(),
) {
    fun toUnableEntity(): BdasAsgnAprv {
        return BdasAsgnAprv(
            id = BdasAsgnAprvId(
                ptId = this.ptId,
                bdasSeq = this.bdasSeq,
                asgnReqSeq = 1,
            ),
            aprvYn = "N",
            negCd = this.negCd,
            msg = this.msg,
            asgnStat = AsgnStat.WAIT.value,
        )
    }

    fun toEntityWhenInHosp(): BdasAsgnAprv {
        return BdasAsgnAprv(
            id = BdasAsgnAprvId(
                ptId = this.ptId,
                bdasSeq = this.bdasSeq,
                asgnReqSeq = 1,
            ),
            aprvYn = "Y",
            msg = this.msg,
            asgnStat = AsgnStat.WAIT.value,
        )
    }

    fun toEntityWhenNotInHosp(asgnReqSeq: Int, hospId: String,
                              hospNm: String): BdasAsgnAprv {
        return BdasAsgnAprv(
            id = BdasAsgnAprvId(
                ptId = this.ptId,
                bdasSeq = this.bdasSeq,
                asgnReqSeq = asgnReqSeq,
            ),
            aprvYn = "Y",
            msg = this.msg,
            reqHospId = hospId,
            reqHospNm = hospNm,
            asgnStat = AsgnStat.WAIT.value,
        )
    }
}

data class BdasAprvDto(
    val ptId: String,
    val bdasSeq: Int,
    val aprvYn: String,
    val negCd: String?,
    val msg: String?,
    val roomNm: String?,
    val deptNm: String?,
    val spclId: String?,
    val spclNm: String?,
    val chrgTelno: String?,
) {
    fun toUnableEntity(): BdasAprv {
        return BdasAprv(
            id = BdasAprvId(
                ptId = this.ptId,
                bdasSeq = this.bdasSeq,
            ),
            aprvYn = "N",
            negCd = this.negCd,
            msg = this.msg,
        )
    }

    fun toEntity(): BdasAprv {
        return BdasAprv(
            id = BdasAprvId(
                ptId = this.ptId,
                bdasSeq = this.bdasSeq,
            ),
            aprvYn = "Y",
            msg = this.msg,
            roomNm = this.roomNm,
            deptNm = this.deptNm,
            spclNm = this.spclNm,

        )
    }
}