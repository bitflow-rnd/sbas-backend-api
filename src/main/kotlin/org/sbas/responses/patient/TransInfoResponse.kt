package org.sbas.responses.patient

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
    val deptNm: String?,
    val spclNm: String?,
    val chrgTelno: String?,
    val msg: String?,
)