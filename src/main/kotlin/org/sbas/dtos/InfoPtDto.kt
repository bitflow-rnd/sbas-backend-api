package org.sbas.dtos

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

class InfoPtSearchDto(
    val ptId: String?,
    val bdasSeq: Int?,
    val ptNm: String?,
    val gndr: String?,
    val rrno1: String?,
    val rrno2: String?,
    val dstr1Cd: String?,
    val dstr2Cd: String?,
    val telno: String?,
    val natiCd: String?,
    val bedStatCd: BedStat?,
    val bedStatNm: String?,
    val updtDttm: Instant?,
//    val statCd: String,
//    val statNm: String,
    val ptTypeCd: String?,
    val svrtTypeCd: String?,
    val undrDsesCd: String?,
    val admsStatCd: String?,
    val admsStatNm: String?,
    var age: Int,
//    var list: MutableList<String> = mutableListOf("")
    var list: String?,
)


class InfoPtSearchDto2 {
    var ptId: String?
    var bdasSeq: Int?
    var ptNm: String?
    var gndr: String?
    var rrno1: String?
    var rrno2: String?
    var dstr1Cd: String?
    var dstr2Cd: String?
    var telno: String?
    var natiCd: String?
    var bedStatCd: BedStat?
    var bedStatNm: String?
    var updtDttm: Instant?
    var ptTypeCd: String?
    var svrtTypeCd: String?
    var undrDsesCd: String?
    var admsStatCd: String?
    var admsStatNm: String?
    var age: Int?
    var tagList: MutableList<String>? = mutableListOf()

    constructor(
        ptId: String?,
        bdasSeq: Int?,
        ptNm: String?,
        gndr: String?,
        rrno1: String?,
        rrno2: String?,
        dstr1Cd: String?,
        dstr2Cd: String?,
        telno: String?,
        natiCd: String?,
        bedStatCd: BedStat?,
        bedStatNm: String?,
        updtDttm: Instant?,
        ptTypeCd: String?,
        svrtTypeCd: String?,
        undrDsesCd: String?,
        admsStatCd: String?,
        admsStatNm: String?,
        age: Int?,
        tagList: MutableList<String>?
    ) {
        this.ptId = ptId
        this.bdasSeq = bdasSeq
        this.ptNm = ptNm
        this.gndr = gndr
        this.rrno1 = rrno1
        this.rrno2 = rrno2
        this.dstr1Cd = dstr1Cd
        this.dstr2Cd = dstr2Cd
        this.telno = telno
        this.natiCd = natiCd
        this.bedStatCd = bedStatCd
        this.bedStatNm = bedStatNm
        this.updtDttm = updtDttm
        this.ptTypeCd = ptTypeCd
        this.svrtTypeCd = svrtTypeCd
        this.undrDsesCd = undrDsesCd
        this.admsStatCd = admsStatCd
        this.admsStatNm = admsStatNm
        this.age = age
        this.tagList = mutableListOf()
    }

    constructor(
        ptId: String?,
        bdasSeq: Int?,
        ptNm: String?,
        gndr: String?,
        rrno1: String?,
        rrno2: String?,
        dstr1Cd: String?,
        dstr2Cd: String?,
        telno: String?,
        natiCd: String?,
        bedStatCd: BedStat?,
        bedStatNm: String?,
        updtDttm: Instant?,
        ptTypeCd: String?,
        svrtTypeCd: String?,
        undrDsesCd: String?,
        admsStatCd: String?,
        admsStatNm: String?,
        age: Int?,
    ) {
        this.ptId = ptId
        this.bdasSeq = bdasSeq
        this.ptNm = ptNm
        this.gndr = gndr
        this.rrno1 = rrno1
        this.rrno2 = rrno2
        this.dstr1Cd = dstr1Cd
        this.dstr2Cd = dstr2Cd
        this.telno = telno
        this.natiCd = natiCd
        this.bedStatCd = bedStatCd
        this.bedStatNm = bedStatNm
        this.updtDttm = updtDttm
        this.ptTypeCd = ptTypeCd
        this.svrtTypeCd = svrtTypeCd
        this.undrDsesCd = undrDsesCd
        this.admsStatCd = admsStatCd
        this.admsStatNm = admsStatNm
        this.age = age
    }
}

