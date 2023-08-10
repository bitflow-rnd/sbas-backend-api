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

    fun toEntityWhenInHosp(): BdasReqAprv {
        return BdasReqAprv(
            id = BdasReqAprvId(
                ptId = this.ptId,
                bdasSeq = this.bdasSeq,
                asgnReqSeq = 1,
            ),
            aprvYn = "Y",
            msg = this.msg,
            reqHospId = null,
            reqHospNm = null,
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
)