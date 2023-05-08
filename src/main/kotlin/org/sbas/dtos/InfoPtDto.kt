package org.sbas.dtos

import com.fasterxml.jackson.annotation.JsonIgnore
import org.sbas.constants.BedStat
import org.sbas.entities.info.InfoPt
import org.sbas.utils.NoArg
import java.time.Instant


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

class InfoPtSearchDto {
    var ptId: String?
    var bdasSeq: Int?
    var ptNm: String?
    var gndr: String?
    var dstr1Cd: String?
    var dstr1CdNm: String?
    var dstr2Cd: String?
    var dstr2CdNm: String?
    var hospId: String?
    var hospNm: String?
    var mpno: String?
    var natiCd: String?
    var statCd: BedStat?
    var statCdNm: String?
    var updtDttm: Instant?
    @JsonIgnore
    var ptTypeCd: String?
    @JsonIgnore
    var svrtTypeCd: String?
    @JsonIgnore
    var undrDsesCd: String?
    var age: Int?
    var tagList: MutableList<String>? = mutableListOf()

    constructor(
        ptId: String?,
        bdasSeq: Int?,
        ptNm: String?,
        gndr: String?,
        dstr1Cd: String?,
        dstr1CdNm: String?,
        dstr2Cd: String?,
        dstr2CdNm: String?,
        hospId: String?,
        hospNm: String?,
        mpno: String?,
        natiCd: String?,
        statCd: BedStat?,
        statCdNm: String?,
        updtDttm: Instant?,
        ptTypeCd: String?,
        svrtTypeCd: String?,
        undrDsesCd: String?,
        age: Int?,
        tagList: MutableList<String>?
    ) {
        this.ptId = ptId
        this.bdasSeq = bdasSeq
        this.ptNm = ptNm
        this.gndr = gndr
        this.dstr1Cd = dstr1Cd
        this.dstr1CdNm = dstr1CdNm
        this.dstr2Cd = dstr2Cd
        this.dstr2CdNm = dstr2CdNm
        this.hospId = hospId
        this.hospNm = hospNm
        this.mpno = mpno
        this.natiCd = natiCd
        this.statCd = statCd
        this.statCdNm = statCdNm
        this.updtDttm = updtDttm
        this.ptTypeCd = ptTypeCd
        this.svrtTypeCd = svrtTypeCd
        this.undrDsesCd = undrDsesCd
        this.age = age
        this.tagList = mutableListOf()
    }

    constructor(
        ptId: String?,
        bdasSeq: Int?,
        ptNm: String?,
        gndr: String?,
        dstr1Cd: String?,
        dstr1CdNm: String?,
        dstr2Cd: String?,
        dstr2CdNm: String?,
        hospId: String?,
        hospNm: String?,
        mpno: String?,
        natiCd: String?,
        statCd: BedStat?,
        statCdNm: String?,
        updtDttm: Instant?,
        ptTypeCd: String?,
        svrtTypeCd: String?,
        undrDsesCd: String?,
        age: Int?,
    ) {
        this.ptId = ptId
        this.bdasSeq = bdasSeq
        this.ptNm = ptNm
        this.gndr = gndr
        this.dstr1Cd = dstr1Cd
        this.dstr1CdNm = dstr1CdNm
        this.dstr2Cd = dstr2Cd
        this.dstr2CdNm = dstr2CdNm
        this.hospId = hospId
        this.hospNm = hospNm
        this.mpno = mpno
        this.natiCd = natiCd
        this.statCd = statCd
        this.statCdNm = statCdNm
        this.updtDttm = updtDttm
        this.ptTypeCd = ptTypeCd
        this.svrtTypeCd = svrtTypeCd
        this.undrDsesCd = undrDsesCd
        this.age = age
    }
}

