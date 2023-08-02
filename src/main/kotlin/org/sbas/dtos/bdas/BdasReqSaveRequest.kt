package org.sbas.dtos.bdas

import org.sbas.constants.enums.BedStatCd
import org.sbas.constants.enums.SvrtTypeCd
import org.sbas.entities.bdas.BdasReq
import org.sbas.entities.bdas.BdasReqId
import org.sbas.utils.StringUtils
import org.sbas.utils.annotation.ValidEnum
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

/**
 * 병상 요청 DTO
 */
data class BdasReqSaveRequest(
    @field: Valid
    val svrInfo: BdasReqSvrInfo,
    @field: Valid
    val dprtInfo: BdasReqDprtInfo,
) {

    fun toEntity(bdasReqId: BdasReqId): BdasReq {
        val entity = svrInfo.toEntity(bdasReqId)

        // 요청 시간 설정
        dprtInfo.reqDt = StringUtils.getYyyyMmDd()
        dprtInfo.reqTm = StringUtils.getHhMmSs()

        // 출발지 정보 저장
        entity.saveDprtInfoFrom(dprtInfo)

        return entity
    }

    fun isPtIdEqual(): Boolean {
        return svrInfo.ptId == dprtInfo.ptId
    }
}


/**
 * 중증 정보
 */
data class BdasReqSvrInfo(
    @field: NotBlank
    var ptId: String,
    @field: NotBlank
    var ptTypeCd: String,
    @field: NotBlank
    var undrDsesCd: String,
    @field: NotBlank
    var reqBedTypeCd: String,
    @field: NotBlank
    var dnrAgreYn: String,
    @field: NotBlank
    var svrtIptTypeCd: String,
    @field: [NotBlank ValidEnum(enumClass = SvrtTypeCd::class)]
    var svrtTypeCd: String,

    var undrDsesEtc: String?,
    var avpuCd: String?,
    var oxyYn: String?,
    var bdtp: Float?,
    var hr: Int?,
    var resp: Int?,
    var spo2: Int?,
    var sbp: Int?,
    var newsScore: Int?,
) {
    fun toEntity(bdasReqId: BdasReqId): BdasReq {
        return BdasReq(
            id = bdasReqId,
            reqDt = "",
            reqTm = "",
            ptTypeCd = this.ptTypeCd,
            reqDstr1Cd = "",
            dprtDstrTypeCd = "",
            inhpAsgnYn = "",
            undrDsesCd = this.undrDsesCd,
            undrDsesEtc = this.undrDsesEtc,
            reqBedTypeCd = this.reqBedTypeCd,
            dnrAgreYn = this.dnrAgreYn,
            svrtTypeCd = this.svrtTypeCd,
            svrtIptTypeCd = this.svrtIptTypeCd,
            avpuCd = this.avpuCd,
            oxyYn = this.oxyYn,
            bdtp = this.bdtp,
            hr = this.hr,
            resp = this.resp,
            spo2 = this.spo2,
            sbp = this.sbp,
            newsScore = this.newsScore,
            bedStatCd = BedStatCd.BAST0003.name
        )
    }
}

/**
 * 출발지 정보
 */
data class BdasReqDprtInfo (
    var ptId: String,

    @field: NotBlank
    var reqDstr1Cd: String,
    var reqDstr2Cd: String? = null,
    
    // 출발지 정보
    @field: NotBlank
    var dprtDstrTypeCd: String,
    var dprtDstrBascAddr: String? = null,
    var dprtDstrDetlAddr: String? = null,
    var dprtDstrZip: String? = null,
    var dprtDstrLat: String? = null,
    var dprtDstrLon: String? = null,
    
    // 보호자 정보
    var nok1Telno: String? = null,
    var nok2Telno: String? = null,
    
    // 담당 병원 정보
    @field: [NotBlank Pattern(regexp = "^[YN]\$", message = "Y/N 값만 가능합니다.")]
    var inhpAsgnYn: String,
    var deptNm: String? = null,
    var spclNm: String? = null,
    var chrgTelno: String? = null,
    var msg: String? = null,
) {
    var reqDt: String = ""
    var reqTm: String = ""
}