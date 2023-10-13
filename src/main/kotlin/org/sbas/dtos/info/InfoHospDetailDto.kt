package org.sbas.dtos.info

import org.sbas.entities.info.FacilityStatus
import org.sbas.entities.info.InfoHospDetail
import org.sbas.entities.info.MedicalTeamCount

data class InfoHospDetailRequest(
    val hospId: String,
    val facilityStatus: FacilityStatus,
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

fun InfoHospDetailRequest.toInfoHospDetail(): InfoHospDetail {
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