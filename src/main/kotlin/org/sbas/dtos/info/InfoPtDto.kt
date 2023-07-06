package org.sbas.dtos.info

import com.fasterxml.jackson.annotation.JsonIgnore
import org.sbas.entities.info.InfoPt
import org.sbas.utils.NoArg
import java.time.Instant


@NoArg
data class InfoPtDto(
    val ptNm: String,
    val gndr: String,
    val rrno1: String,
    val rrno2: String,
    var dstr1Cd: String,
    var dstr2Cd: String,
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

fun InfoPtDto.toEntity(dstr1Cd: String, dstr2Cd: String): InfoPt {
    return InfoPt(
        ptNm = this.ptNm,
        gndr = this.gndr,
        rrno1 = this.rrno1,
        rrno2 = this.rrno2,
        dstr1Cd = dstr1Cd,
        dstr2Cd = dstr2Cd,
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

/**
 * 환자 목록 조회용 DTO
 */
data class InfoPtSearchDto(
    var ptId: String?,
    var bdasSeq: Int?,
    var ptNm: String?,
    var gndr: String?,
    var dstr1Cd: String?,
    var dstr1CdNm: String?,
    var dstr2Cd: String?,
    var dstr2CdNm: String?,
    var hospId: String?,
    var hospNm: String?,
    var mpno: String?,
    var natiCd: String?,
    var statCd: String?,
    var statCdNm: String?,
    var updtDttm: Instant?,
    @JsonIgnore var ptTypeCd: String?,
    @JsonIgnore var svrtTypeCd: String?,
    @JsonIgnore var undrDsesCd: String?,
    var age: Int?,
) {
    var tagList: MutableList<String>? = mutableListOf()
}

data class InfoPtBasicInfo(
    val ptId: String?,
    val ptNm: String?,
    val gndr: String?,
    val age: Int?,
    val rrno1: String?,
    val rrno2: String?,
    val bascAddr: String?,
    val detlAddr: String?,
    val dethYn: String?,
    val natiCd: String?,
    val natiNm: String?,
    val mpno: String?,
    val telno: String?,
    val nokNm: String?,
    val job: String?,
    val attcId: String?,
    val bedStatCd: String?,
    val bedStatNm: String?,
    val undrDsesCd: List<String>?,
)

data class BdasHisInfo(
    val ptId: String?,
    val bdasSeq: Int?,
    val diagNm: String?,
    val hospNm: String?,
    var order: String?,
    val updtDttm: Instant?,
    @JsonIgnore val ptTypeCd: String?,
    @JsonIgnore val svrtTypeCd: String?,
    @JsonIgnore val undrDsesCd: String?,
) {
    var tagList: MutableList<String>? = mutableListOf()
}