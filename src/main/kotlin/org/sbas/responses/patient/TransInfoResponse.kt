package org.sbas.responses.patient

import org.sbas.constants.enums.DprtTypeCd
import org.sbas.constants.enums.SidoCd
import org.sbas.entities.bdas.BdasReq
import org.sbas.entities.bdas.BdasTrns

data class TransInfoResponse(
    val ptId: String,
    val bdasSeq: Int,
    val reqDstr1Cd: String,
    val reqDstr1CdNm: String,
    val dprtDstrTypeCd: String,
    val dprtDstrTypeCdNm: String,
    val dprtDstrBascAddr: String?,
    val dprtDstrDetlAddr: String?,
    val dprtDstrLat: String?,
    val dprtDstrLon: String?,
    val nok1Telno: String?,
    val nok2Telno: String?,
    val inhpAsgnYn: String,
    val msg: String?,

    // 이송정보
    val ambsNm: String?,
    val vecno: String?,
    val chfTelno: String?,
    val crewNm: String?,
    val destinationInfo: DestinationInfo,
)

data class DestinationInfo(
    val hospId: String,
    val hospNm: String,
    val hospTelno: String,
    val hospAddr: String,
    val destinationLat: String,
    val destinationLon: String,
    val roomNm: String?,
    val deptNm: String?,
    val spclNm: String?,
    val msg: String?,
)

fun BdasReq.toTransInfoResponse(bdasTrns: BdasTrns, destinationInfo: DestinationInfo): TransInfoResponse {
    return TransInfoResponse(
        ptId = this.id.ptId,
        bdasSeq = this.id.bdasSeq,
        reqDstr1Cd = this.reqDstr1Cd,
        reqDstr1CdNm = SidoCd.valueOf("SIDO${this.reqDstr1Cd}").cdNm,
        dprtDstrTypeCd = this.dprtDstrTypeCd,
        dprtDstrTypeCdNm = DprtTypeCd.valueOf(this.dprtDstrTypeCd).cdNm,
        dprtDstrBascAddr = this.dprtDstrBascAddr,
        dprtDstrDetlAddr = this.dprtDstrDetlAddr,
        dprtDstrLat = this.dprtDstrLat,
        dprtDstrLon = this.dprtDstrLon,
        nok1Telno = this.nok1Telno,
        nok2Telno = this.nok2Telno,
        inhpAsgnYn = this.inhpAsgnYn,
        msg = this.msg,
        ambsNm = bdasTrns.ambsNm,
        vecno = bdasTrns.vecno,
        chfTelno = bdasTrns.chfTelno,
        crewNm = listOfNotNull(
            bdasTrns.crew1Nm?.takeIf { it.isNotEmpty() },
            bdasTrns.crew2Nm?.takeIf { it.isNotEmpty() },
            bdasTrns.crew3Nm?.takeIf { it.isNotEmpty() },
        ).joinToString(", "),
        destinationInfo = destinationInfo,
    )
}
