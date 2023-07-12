package org.sbas.dtos.bdas

import org.sbas.utils.NoArg

/**
 * 출발지 정보
 */
@NoArg
data class BdasReqDprtInfo (
    var ptId: String,
    var reqDt: String? = null,
    var reqTm: String? = null,

    var reqDstr1Cd: String? = null,
    var reqDstr2Cd: String? = null,
    
    // 출발지 정보
    var dprtDstrTypeCd: String? = null,
    var dprtDstrBascAddr: String? = null,
    var dprtDstrDetlAddr: String? = null,
    var dprtDstrZip: String? = null,
    var dprtDstrLat: String? = null,
    var dprtDstrLon: String? = null,
    
    // 보호자 정보
    var nok1Telno: String? = null,
    var nok2Telno: String? = null,
    
    // 담당 병원 정보
    var inhpAsgnYn: String? = null,
    var deptNm: String? = null,
    var spclNm: String? = null,
    var chrgTelno: String? = null,
    var msg: String? = null,
)

/**
 * 중증 정보
 */
@NoArg
data class BdasReqSvrInfo(
    var ptId: String,

    var ptTypeCd: String?,
    var undrDsesCd: String?,
    var undrDsesEtc: String?,
    var reqBedTypeCd: String?,
    var dnrAgreYn: String?,
    var svrtIptTypeCd: String?,

    var avpuCd: String?,
    var oxyYn: String?,
    var bdtp: Float?,
    var hr: Int?,
    var resp: Int?,
    var spo2: Int?,
    var sbp: Int?,
    var newsScore: Int?,

    var svrtTypeCd: String?,
)