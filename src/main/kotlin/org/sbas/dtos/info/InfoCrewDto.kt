package org.sbas.dtos.info

import org.sbas.entities.info.InfoCrew
import org.sbas.entities.info.InfoCrewId
import org.sbas.utils.NoArg

@NoArg
data class InfoCrewRegDto (
    var instId : String,
    var crewId : String,
    var crewNm : String,
    var telno : String,
    var rmk : String?,
    var pstn : String?,
) {
    fun toEntityForInsert(adminId: String): InfoCrew {
        val infoCrewId = InfoCrewId(instId, crewId)
        return InfoCrew(
            id = infoCrewId,
            crewNm = crewNm,
            telno = telno,
            rmk = rmk,
            pstn = pstn
        )
    }
}