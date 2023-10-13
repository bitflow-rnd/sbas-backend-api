package org.sbas.entities.info

import org.sbas.entities.CommonEntity
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

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
}


@Embeddable
data class FacilityStatus(
    @Column(name = "child_birth_yn", nullable = false)
    val childBirthYn: Boolean,
    @Column(name = "dialysis_yn", nullable = false)
    val dialysisYn: Boolean,
    @Column(name = "child_yn", nullable = false)
    val childYn: Boolean,
    @Column(name = "nursing_hospital_yn", nullable = false)
    val nursingHospitalYn: Boolean,
    @Column(name = "mental_patient_yn", nullable = false)
    val mentalPatientYn: Boolean,
    @Column(name = "negative_pressure_room_yn", nullable = false)
    val negativePressureRoomYn: Boolean,
)

@Embeddable
data class MedicalTeamCount(
    @Column(name = "child_birth_med", nullable = false)
    val childBirthMed: Int,
    @Column(name = "dialysis_med", nullable = false)
    val dialysisMed: Int,
    @Column(name = "child_med", nullable = false)
    val childMed: Int,
    @Column(name = "nursing_hospital_med", nullable = false)
    val nursingHospitalMed: Int,
    @Column(name = "mental_patient_med", nullable = false)
    val mentalPatientMed: Int,
)