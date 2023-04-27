package org.sbas.dtos

import org.sbas.entities.bdas.BdasReq
import org.sbas.entities.bdas.BdasReqId
import org.sbas.utils.NoArg

data class BdasReqDto(
    var ptId: String,
    var bdasSeq: Int,
    var histCd: String,
    var reqDt: String? = null,
    var reqTm: String? = null,

    var reqTypeCd: String? = null,
    var reqDstr1Cd: String? = null,
    var reqDstr2Cd: String? = null,
    var dprtDstrTypeCd: String? = null,
    var dprtDstrBascAddr: String? = null,
    var dprtDstrDetlAddr: String? = null,
    var dprtDstrZip: String? = null,
    var dprtDstrLat: String? = null,
    var dprtDstrLon: String? = null,
    var nok1Telno: String? = null,
    var nok2Telno: String? = null,
    var dprtHospId: String? = null,
    var inhpAsgnYn: String? = null,
    var deptNm: String? = null,
    var spclNm: String? = null,
    var chrgTelno: String? = null,
    var msg: String? = null,
) {

}

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
    var whoScore: Int?,
    var svrtTypeCd: String?,
    var svrtTypeCdAnly: String?,
) {

    fun toSvrtInfoEntity(bdasReqId: BdasReqId): BdasReq {
        return BdasReq(
            id = bdasReqId,
            histCd = "Y",
            reqDt = "",
            reqTm = "",
            ptTypeCd = this.ptTypeCd,
            undrDsesCd = this.undrDsesCd,
            undrDsesEtc = this.undrDsesEtc,
            reqBedTypeCd = this.reqBedTypeCd,
            dnrAgreYn = this.dnrAgreYn,
            svrtIptTypeCd = this.svrtIptTypeCd,
            svrtTypeCd = this.svrtTypeCd,
            avpuCd = this.avpuCd,
            oxyYn = this.oxyYn,
            bdtp = this.bdtp,
            hr = this.hr,
            resp = this.resp,
            spo2 = this.spo2,
            sbp = this.sbp,
            newsScore = this.newsScore,
            reqTypeCd = "",
            reqDstr1Cd = "",
            dprtDstrTypeCd = "",
            inhpAsgnYn = "",
        )
    }

}

@NoArg
data class BdasReqBioInfo(
    var svrtIptTypeCd: String?,
    var avpuCd: String?,
    var oxyYn: String?,
    var bdtp: Float?,
    var hr: Int?,
    var resp: Int?,
    var spo2: Int?,
    var sbp: Int?,
    var newsScore: Int?,
    var whoScore: Int?,
    var svrtTypeCd: String?,
) {
}