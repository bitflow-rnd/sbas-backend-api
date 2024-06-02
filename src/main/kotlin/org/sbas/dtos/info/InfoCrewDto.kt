package org.sbas.dtos.info

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import jakarta.ws.rs.QueryParam
import org.sbas.entities.info.InfoCrew
import org.sbas.entities.info.InfoCrewId
import org.sbas.utils.annotation.NoArg

data class InfoCrewSaveReq (
    @field: [NotBlank(message = "기관 ID는 필수 값입니다.") Size(max = 10)]
    var instId : String,
    @field: [NotBlank(message = "구급대원 이름은 필수 값입니다.") Size(max = 12)]
    var crewNm : String,
    @field: Size(max = 12)
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
    @field: [NotBlank(message = "기관 ID는 필수 값입니다.") Size(max = 10)]
    var instId : String,
    @field: NotNull(message = "구급대원 ID는 필수 값입니다.")
    var crewId : Int,
    @field: [NotBlank(message = "구급대원 이름은 필수 값입니다.") Size(max = 12)]
    var crewNm : String,
    var telno : String?,
    var rmk : String?,
    var pstn : String?,
)

data class InfoCrewDelReq(
    val instId: String,
    val crewId: Int,
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