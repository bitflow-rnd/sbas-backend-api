package org.sbas.dtos.info

import org.sbas.entities.info.InfoInst
import org.sbas.utils.NoArg
import javax.ws.rs.QueryParam

@NoArg
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

@NoArg
data class FireStatnSaveReq(
    var instId: String,
    var instNm: String,
    var dstrCd1: String,
    var dstrCd2: String,
    var chrgId: String,
    var chrgNm: String,
    var chrgTelno: String,
    var rmk: String,
    var baseAddr: String,
    var lat: String,
    var lon: String,
) {
    fun toEntity(): InfoInst {
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
            baseAddr = baseAddr,
            lat = lat,
            lon = lon,
        )
    }
}

@NoArg
data class FireStatnSearchParam(
    @field: QueryParam("instId") val instId: String?,
    @field: QueryParam("instNm") val instNm: String?,
    @field: QueryParam("dstrCd1") val dstrCd1: String?,
    @field: QueryParam("dstrCd2") val dstrCd2: String?,
    @field: QueryParam("chrgTelno") val chrgTelno: String?,
)

@NoArg
data class FireStatnListDto(
    val instId: String,
    val instNm: String?,
    val dstrCd1: String?,
    val dstrCd2: String?,
    val chrgTelno: String?,
) {
    var crewCount: Long = 0L
}

data class CrewCountList(
    val crewCount: Long,
    val instId: String,
)