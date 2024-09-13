package org.sbas.dtos.bdas

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import org.sbas.entities.bdas.BdasReqAprv
import org.sbas.entities.bdas.BdasReqAprvId
import org.sbas.entities.info.FacilityStatus
import org.sbas.entities.info.MedicalTeamCount

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
  val dutyDivNam: String?,
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
  val ventilator: String?,
  val ventilatorPreemie: String?,
  val incubator: String?,
  val ecmo: String?,
  val highPressureOxygen: String?,
  val ct: String?,
  val mri: String?,
  val bloodVesselImaging: String?,
  val bodyTemperatureControl: String?,
  var facilityStatus: FacilityStatus,
  var medicalTeamCount: MedicalTeamCount,
) {
    val tagList: MutableList<String>
      get() = mutableListOf<String>().apply {
        if (gnbdIcu >= 1) add("중환자")
        if (npidIcu >= 1) add("음압격리")
        if (gnbdSvrt >= 1) add("중증")
        if (gnbdSmsv >= 1) add("준중증")
        if (gnbdModr >= 1) add("중등증")
        facilityStatus.let {
          if (it.childBirthYn && medicalTeamCount.childBirthMed > 0) add("분만")
          if (it.dialysisYn && medicalTeamCount.dialysisMed > 0) add("투석")
          if (it.childYn && medicalTeamCount.childMed > 0) add("소아")
          if (it.nursingHospitalYn && medicalTeamCount.nursingHospitalMed > 0) add("요양병원")
          if (it.mentalPatientYn && medicalTeamCount.mentalPatientMed > 0) add("정신질환자")
          if (it.negativePressureRoomYn) add("음압수술실")
        }
      }
}