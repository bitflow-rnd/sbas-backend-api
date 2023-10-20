package org.sbas.dtos.info

import org.sbas.entities.info.FacilityStatus
import org.sbas.entities.info.InfoHospDetail
import org.sbas.entities.info.MedicalTeamCount

data class InfoHospDetailDto(
    val hospId: String,
    val childBirthYn: Boolean,
    val dialysisYn: Boolean,
    val childYn: Boolean,
    val nursingHospitalYn: Boolean,
    val mentalPatientYn: Boolean,
    val negativePressureRoomYn: Boolean,
    val childBirthMed: Int,
    val dialysisMed: Int,
    val childMed: Int,
    val nursingHospitalMed: Int,
    val mentalPatientMed: Int,
)

fun InfoHospDetailDto.toInfoHospDetail(): InfoHospDetail {
    return InfoHospDetail(
        hospId = hospId,
        facilityStatus = FacilityStatus(
            childBirthYn = childBirthYn,
            dialysisYn = dialysisYn,
            childYn = childYn,
            nursingHospitalYn = nursingHospitalYn,
            mentalPatientYn = mentalPatientYn,
            negativePressureRoomYn = negativePressureRoomYn,
        ),
        medicalTeamCount = MedicalTeamCount(
            childBirthMed = childBirthMed,
            dialysisMed = dialysisMed,
            childMed = childMed,
            nursingHospitalMed = nursingHospitalMed,
            mentalPatientMed = mentalPatientMed,
        ),
    )
}