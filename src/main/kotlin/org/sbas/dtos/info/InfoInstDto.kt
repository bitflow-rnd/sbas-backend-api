package org.sbas.dtos.info

import org.sbas.entities.info.InfoInst
import org.sbas.utils.NoArg

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

