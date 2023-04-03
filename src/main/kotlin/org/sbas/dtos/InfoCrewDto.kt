package org.sbas.dtos

import org.eclipse.microprofile.jwt.JsonWebToken
import org.sbas.entities.info.InfoCrew
import org.sbas.entities.info.InfoCrewId
import org.sbas.utils.NoArg
import javax.inject.Inject

@NoArg
data class InfoCrewRegDto (
    var instId : String,
    var crewId : String,
    var crewNm : String,
    var telno : String,
    var rmk : String?,
    var pstn : String?,
    var adminId: String?,
) {

    fun toEntity(): InfoCrew {
        val infoCrewId = InfoCrewId(instId, crewId)
        val infoCrew = InfoCrew(
            id = infoCrewId,
            crewNm = crewNm,
            telno = telno,
            rmk = rmk,
            pstn = pstn
        )
        infoCrew.rgstUserId = adminId
        infoCrew.updtUserId = adminId
        return infoCrew
    }
}