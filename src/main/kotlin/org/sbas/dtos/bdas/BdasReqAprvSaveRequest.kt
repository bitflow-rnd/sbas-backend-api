package org.sbas.dtos.bdas

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.sbas.entities.bdas.BdasReqAprv
import org.sbas.entities.bdas.BdasReqAprvId
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

data class BdasReqAprvSaveRequest(
    @field: NotBlank
    val ptId: String,
    @field: NotNull
    val bdasSeq: Int,
    @field: [NotBlank Pattern(regexp = "^[YN]\$", message = "Y/N 값만 가능합니다.")]
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

    fun toEntityWhenInHosp(hospId: String?, hospNm: String?): BdasReqAprv {
        return BdasReqAprv(
            id = BdasReqAprvId(
                ptId = this.ptId,
                bdasSeq = this.bdasSeq,
                asgnReqSeq = 1,
            ),
            aprvYn = "Y",
            msg = this.msg,
            reqHospId = hospId,
            reqHospNm = hospNm,
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

data class BdasAprvResponse(
    @JsonProperty("isAlreadyApproved")
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
    val gnbdIcu: Int, // hv22 54
    val npidIcu: Int, // hv23 55
    val gnbdSvrt: Int, // hv24 56
    val gnbdSmsv: Int, // hv25
    val gnbdModr: Int, // hv26
) {
    val tagList: MutableList<String>
        get() {
            val list = mutableListOf<String>()
            if (gnbdIcu >= 1) {
                list.add("중환자")
            }
            if (npidIcu >= 1) {
                list.add("음압격리")
            }
            if (gnbdSvrt >= 1) {
                list.add("중증")
            }
            if (gnbdSmsv >= 1) {
                list.add("준중증")
            }
            if (gnbdModr >= 1) {
                list.add("중등증")
            }
            return list
        }
}