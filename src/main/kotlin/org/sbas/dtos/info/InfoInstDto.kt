package org.sbas.dtos.info

import org.sbas.entities.info.InfoInst
import org.sbas.utils.annotation.NoArg
import javax.validation.constraints.NotBlank
import javax.ws.rs.QueryParam

data class InfoInstUpdateReq(
    var instId: String,
    var instNm: String?,
    var dstrCd1: String?,
    var dstrCd2: String?,
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
    @field: NotBlank
    val instNm: String,
    @field: NotBlank
    val dstrCd1: String,
    val dstrCd2: String?,
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
            dstrCd1 = dstrCd1,
            dstrCd2 = dstrCd2,
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
    @field: QueryParam("dstrCd1") var dstrCd1: String?,
    @field: QueryParam("dstrCd2") var dstrCd2: String?,
    @field: QueryParam("chrgTelno") var chrgTelno: String?,
    @field: QueryParam("page") var page: Int? = 0,
)

data class FireStatnListDto(
    val instId: String,
    val instNm: String?,
    val dstrCd1: String?,
    val dstrCd2: String?,
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
    val dstrCd1: String,
    val dstrCd1Nm: String,
    val dstrCd2: String?,
    val dstrCd2Nm: String?,
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