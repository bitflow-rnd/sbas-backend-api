package org.sbas.dtos.info

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.ws.rs.QueryParam
import org.sbas.entities.info.InfoInst
import org.sbas.utils.annotation.NoArg

data class InfoInstUpdateReq(
    @field: [NotBlank(message = "기관 ID는 필수 값입니다.") Size(max = 8)]
    var instId: String,
    @field: [NotBlank(message = "기관 이름은 필수 값입니다.")]
    var instNm: String,
    @field: NotBlank
    var dstr1Cd: String,
    var dstr2Cd: String?,
    var chrgId: String?,
    var chrgNm: String?,
    var chrgTelno: String?,
    var rmk: String?,
    var baseAddr: String?,
    var lat: String?,
    var lon: String?,
    var attcId: String?,
)

data class FireStatnSaveReq(
    @field: [NotBlank(message = "기관 이름은 필수 값입니다.")]
    val instNm: String,
    @field: NotBlank
    val dstr1Cd: String,
    val dstr2Cd: String?,
    val chrgId: String?,
    val chrgNm: String?,
    val chrgTelno: String?,
    val rmk: String?,
    val detlAddr: String?,
    var lat: String?,
    var lon: String?,
) {
    fun toEntity(instId: String): InfoInst {
        return InfoInst(
            id = instId,
            instTypeCd = "ORGN0002",
            instNm = instNm,
            dstr1Cd = dstr1Cd,
            dstr2Cd = dstr2Cd,
            chrgId = chrgId,
            chrgNm = chrgNm,
            chrgTelno = chrgTelno,
            rmk = rmk,
            detlAddr = detlAddr,
            lat = lat,
            lon = lon,
        )
    }
}

@NoArg
data class FireStatnSearchParam(
    @field: QueryParam("instId") var instId: String?,
    @field: QueryParam("instNm") var instNm: String?,
    @field: QueryParam("dstr1Cd") var dstr1Cd: String?,
    @field: QueryParam("dstr2Cd") var dstr2Cd: String?,
    @field: QueryParam("chrgTelno") var chrgTelno: String?,
    @field: QueryParam("page") var page: Int? = 0,
)

data class FireStatnListDto(
    val instId: String,
    val instNm: String?,
    val dstr1Cd: String?,
    val dstr2Cd: String?,
    val chrgTelno: String?,
    val crewCount: Long?,
    val lat: String?,
    val lon: String?,
)

data class FireStatnDto(
    val instId: String,
    val instNm: String?,
    val chrgId: String?,
    val chrgNm: String?,
    val dstr1Cd: String,
    val dstr1CdNm: String,
    val dstr2Cd: String?,
    val dstr2CdNm: String?,
    val chrgTelno: String?,
    val rmk: String?,
    val detlAddr: String?,
    val lat: String?,
    val lon: String?,
    var vecno: String?,
) {
    init {
        vecno = vecno?.replace(";", ", ")
    }
}

data class InfoInstResponse(
    val instId: String?,
    val instTypeCd: String?,
    val instNm: String?,
    val dstr1Cd: String?,
    val dstr2Cd: String?,
)