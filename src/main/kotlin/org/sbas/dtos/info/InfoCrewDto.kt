package org.sbas.dtos.info

import org.sbas.entities.info.InfoCrew
import org.sbas.entities.info.InfoCrewId
import org.sbas.utils.annotation.NoArg
import javax.ws.rs.QueryParam

data class InfoCrewRegDto (
    var instId : String,
    var crewId : String,
    var crewNm : String?,
    var telno : String?,
    var rmk : String?,
    var pstn : String?,
) {
    fun toEntityForInsert(crewId: String): InfoCrew {
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

@NoArg
data class InfoCrewSearchParam(
    @field: QueryParam("instId") var instId: String,
    @field: QueryParam("crewId") var crewId: String?,
    @field: QueryParam("crewNm") var crewNm: String?,
    @field: QueryParam("telno") var telno: String?,
)

data class InfoCrewDto(
    val instId: String?,
    val crewId: String?,
    val crewNm: String?,
    val telno: String?,
    val rmk: String?,
    val pstn: String?,
)