package org.sbas.dtos.info

import org.sbas.entities.info.InfoCrew
import org.sbas.entities.info.InfoCrewId
import org.sbas.utils.annotation.NoArg
import javax.validation.constraints.NotBlank
import javax.ws.rs.QueryParam

data class InfoCrewSaveReq (
    @field: NotBlank
    var instId : String,
    @field: NotBlank
    var crewNm : String,
    var telno : String?,
    var rmk : String?,
    var pstn : String?,
) {
    fun toEntityForInsert(crewId: Int): InfoCrew {
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

data class InfoCrewUpdateReq(
    @field: NotBlank
    var instId : String,
    @field: NotBlank
    var crewId : Int,
    @field: NotBlank
    var crewNm : String,
    var telno : String?,
    var rmk : String?,
    var pstn : String?,
)

@NoArg
data class InfoCrewSearchParam(
    @field: QueryParam("instId") var instId: String,
    @field: QueryParam("crewId") var crewId: Int?,
    @field: QueryParam("crewNm") var crewNm: String?,
    @field: QueryParam("telno") var telno: String?,
)

data class InfoCrewDto(
    val instId: String,
    val crewId: Int,
    val crewNm: String,
    val telno: String?,
    val rmk: String?,
    val pstn: String?,
)