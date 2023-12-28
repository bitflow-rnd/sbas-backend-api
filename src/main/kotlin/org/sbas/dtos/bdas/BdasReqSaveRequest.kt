package org.sbas.dtos.bdas

import org.sbas.constants.enums.BedStatCd
import org.sbas.constants.enums.ReqBedTypeCd
import org.sbas.constants.enums.SvrtTypeCd
import org.sbas.entities.bdas.BdasReq
import org.sbas.entities.bdas.BdasReqId
import org.sbas.utils.StringUtils
import org.sbas.utils.annotation.ValidEnum
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.sbas.entities.info.UserActivityHistory
import org.sbas.entities.info.UserActivityHistoryId

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
        val ptTypeCd = svrInfo.ptTypeCd?.let { if (it.isEmpty()) "PTTP0001" else it } ?: "PTTP0001"
        return BdasReq(
            id = bdasReqId,
            reqDt = StringUtils.getYyyyMmDd(),
            reqTm = StringUtils.getHhMmSs(),
            ptTypeCd = ptTypeCd,
            undrDsesCd = svrInfo.undrDsesCd,
            undrDsesEtc = svrInfo.undrDsesEtc,
            reqBedTypeCd = svrInfo.reqBedTypeCd,
            dnrAgreYn = svrInfo.dnrAgreYn,
            svrtIptTypeCd = svrInfo.svrtIptTypeCd,
            svrtTypeCd = svrInfo.svrtTypeCd,
            avpuCd = svrInfo.avpuCd,
            oxyYn = svrInfo.oxyYn,
            bdtp = svrInfo.bdtp,
            hr = svrInfo.hr,
            resp = svrInfo.resp,
            spo2 = svrInfo.spo2,
            sbp = svrInfo.sbp,
            newsScore = svrInfo.newsScore,
            reqDstr1Cd = dprtInfo.reqDstr1Cd,
            reqDstr2Cd = dprtInfo.reqDstr2Cd,
            dprtDstrTypeCd = dprtInfo.dprtDstrTypeCd,
            dprtDstrBascAddr = dprtInfo.dprtDstrBascAddr,
            dprtDstrDetlAddr = dprtInfo.dprtDstrDetlAddr,
            dprtDstrZip = dprtInfo.dprtDstrZip,
            dprtDstrLat = dprtInfo.dprtDstrLat,
            dprtDstrLon = dprtInfo.dprtDstrLon,
            nok1Telno = dprtInfo.nok1Telno,
            nok2Telno = dprtInfo.nok2Telno,
            inhpAsgnYn = dprtInfo.inhpAsgnYn,
            deptNm = dprtInfo.deptNm,
            spclNm = dprtInfo.spclNm,
            chrgTelno = dprtInfo.chrgTelno,
            msg = dprtInfo.msg,
            bedStatCd = BedStatCd.BAST0003.name,
        )
    }

    fun toActivityHistory(userId: String): UserActivityHistory {
        return UserActivityHistory(
            id = UserActivityHistoryId(
                userId = userId,
                ptId = svrInfo.ptId,
            ),
            activityDetail = "병상요청",
        )
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
    val ptId: String,
    val ptTypeCd: String?,
    val undrDsesCd: String?,
    @field: [NotBlank ValidEnum(enumClass = ReqBedTypeCd::class)]
    val reqBedTypeCd: String,
    @field: NotBlank
    val dnrAgreYn: String,
    @field: NotBlank
    val svrtIptTypeCd: String,
    @field: [NotBlank ValidEnum(enumClass = SvrtTypeCd::class)]
    val svrtTypeCd: String,

    val undrDsesEtc: String?,
    val avpuCd: String?,
    val oxyYn: String?,
    val bdtp: Float?,
    val hr: Int?,
    val resp: Int?,
    val spo2: Int?,
    val sbp: Int?,
    val newsScore: Int?,
)

/**
 * 출발지 정보
 */
data class BdasReqDprtInfo(
    val ptId: String,

    @field: NotBlank
    val reqDstr1Cd: String,
    val reqDstr2Cd: String?,

    // 출발지 정보
    @field: NotBlank
    val dprtDstrTypeCd: String,
    @field: NotBlank
    val dprtDstrBascAddr: String,
    val dprtDstrDetlAddr: String?,
    val dprtDstrZip: String?,
    var dprtDstrLat: String?,
    var dprtDstrLon: String?,

    // 보호자 정보
    val nok1Telno: String?,
    val nok2Telno: String?,

    // 담당 병원 정보
    @field: [NotBlank Pattern(regexp = "^[YN]\$", message = "Y/N 값만 가능합니다.")]
    val inhpAsgnYn: String,
    val deptNm: String?,
    val spclNm: String?,
    val chrgTelno: String?,
    val msg: String?,
)