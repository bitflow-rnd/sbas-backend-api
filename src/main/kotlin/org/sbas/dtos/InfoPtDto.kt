package org.sbas.dtos

import org.sbas.entities.info.InfoPt

data class InfoPtReq(
    val ptNm: String,
    val gndr: String,
    val rrno1: String,
    val rrno2: String,
    val dstr1Cd: String,
    val dstr2Cd: String,
    val addr: String,
    val telno: String,
    val natiCd: String,
)

fun InfoPtReq.toEntity(): InfoPt {
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
    )
}