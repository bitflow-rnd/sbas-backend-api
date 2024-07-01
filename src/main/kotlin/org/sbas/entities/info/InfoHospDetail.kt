package org.sbas.entities.info

import jakarta.persistence.*
import org.sbas.dtos.info.InfoHospDetailDto
import org.sbas.entities.CommonEntity

@Entity
@Table(name = "info_hosp_detail")
class InfoHospDetail(
    @Id
    @Column(name = "hosp_id", nullable = false, length = 10)
    var hospId: String,

    @Embedded
    var facilityStatus: FacilityStatus,

    @Embedded
    var medicalTeamCount: MedicalTeamCount,
) : CommonEntity() {
    fun update(request: InfoHospDetail) {
        this.facilityStatus = request.facilityStatus
        this.medicalTeamCount = request.medicalTeamCount
    }

    fun toResponse(): InfoHospDetailDto {
        return InfoHospDetailDto(
            hospId = this.hospId,
            childBirthYn = this.facilityStatus.childBirthYn,
            dialysisYn = this.facilityStatus.dialysisYn,
            childYn = this.facilityStatus.childYn,
            nursingHospitalYn = this.facilityStatus.nursingHospitalYn,
            mentalPatientYn = this.facilityStatus.mentalPatientYn,
            negativePressureRoomYn = this.facilityStatus.negativePressureRoomYn,
            childBirthMed = this.medicalTeamCount.childBirthMed,
            dialysisMed = this.medicalTeamCount.dialysisMed,
            childMed = this.medicalTeamCount.childMed,
            nursingHospitalMed = this.medicalTeamCount.nursingHospitalMed,
            mentalPatientMed = this.medicalTeamCount.mentalPatientMed,
        )
    }
}


@Embeddable
data class FacilityStatus(
    @Column(name = "child_birth_yn", nullable = false)
    val childBirthYn: Boolean = false,
    @Column(name = "dialysis_yn", nullable = false)
    val dialysisYn: Boolean = false,
    @Column(name = "child_yn", nullable = false)
    val childYn: Boolean = false,
    @Column(name = "nursing_hospital_yn", nullable = false)
    val nursingHospitalYn: Boolean = false,
    @Column(name = "mental_patient_yn", nullable = false)
    val mentalPatientYn: Boolean = false,
    @Column(name = "negative_pressure_room_yn", nullable = false)
    val negativePressureRoomYn: Boolean = false,
)

@Embeddable
data class MedicalTeamCount(
    @Column(name = "child_birth_med", nullable = false)
    val childBirthMed: Int = 0,
    @Column(name = "dialysis_med", nullable = false)
    val dialysisMed: Int = 0,
    @Column(name = "child_med", nullable = false)
    val childMed: Int = 0,
    @Column(name = "nursing_hospital_med", nullable = false)
    val nursingHospitalMed: Int = 0,
    @Column(name = "mental_patient_med", nullable = false)
    val mentalPatientMed: Int = 0,
)