package org.sbas.dtos

import org.sbas.entities.info.InfoPt
import org.sbas.utils.NoArg

@NoArg
data class InfoPtDto(
    val ptNm: String,
    val gndr: String,
    val rrno1: String,
    val rrno2: String,
    val dstr1Cd: String,
    val dstr2Cd: String,
    val addr: String,
    val telno: String,
    val natiCd: String,
    val picaVer: String,
    val dethYn: String,
    val nokNm: String,
    val mpno: String,
    val job: String,
    val attcId: String,
    val bascAddr: String,
    val detlAddr: String,
    val zip: String,
    val natiNm: String,
)

fun InfoPtDto.toEntity(): InfoPt {
    return InfoPt(
        ptNm = this.ptNm,
        gndr = this.gndr,
        rrno1 = this.rrno1,
        rrno2 = this.rrno2,
        dstr1Cd = this.dstr1Cd,
        dstr2Cd = this.dstr2Cd,
        addr = this.addr,
        telno = this.telno,
        natiCd = this.natiCd,
        picaVer = this.picaVer,
        dethYn = this.dethYn,
        nokNm = this.nokNm,
        mpno = this.mpno,
        job = this.job,
        attcId = this.attcId,
        bascAddr = this.bascAddr,
        detlAddr = this.detlAddr,
        zip = this.zip,
        natiNm = this.natiNm,
    )
}

@NoArg
data class NewsScoreParam(
    var breath: Int,  // 분당호흡수
    var spo2: Int,    // 산소포화도
    var o2Apply: String, // 산소투여여부
    var sbp: Int,     // 수축기혈압
    var pulse: Int,   // 맥박
    var avpu: String,    // 의식상태
    var bdTemp: Float,  // 체온
)