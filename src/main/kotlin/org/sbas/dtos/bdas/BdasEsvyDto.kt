package org.sbas.dtos.bdas

import org.sbas.entities.bdas.BdasEsvy
import org.sbas.entities.info.InfoPt
import org.sbas.utils.NoArg

@NoArg
data class BdasEsvyDto(
    var ptId: String,
    var esvyAttcId: String? = null,

    // InfoPt 에서 가져오는 정보들 -> 역학조사서에도 있음
    var telno: String?,
    var addr: String?,
    var mpno: String?,
    var nokNm: String?,
    var job: String?,
    var dethYn: String?,

    // 역학 조사서에서 가져오는 정보들
    var rcptPhc: String?,
    var diagNm: String?,
    var diagGrde: String?,
    var cv19Symp: String?,
    var occrDt: String?,
    var diagDt: String?,
    var rptDt: String?,
    var dfdgExamRslt: String?,
    var ptCatg: String?,
    var admsYn: String?,
    var rptType: String?,
    var rmk: String?,
    var instNm: String?,
    var instId: String?,
    var instTelno: String?,
    var instAddr: String?,
    var diagDrNm: String?,
    var rptChfNm: String?,

    var instBascAddr: String?,
    var instDetlAddr: String?,
    var instZip: String?,
    var diagAttcId: String?,
) {
    fun toEntity(): BdasEsvy {
        return BdasEsvy(
            ptId = this.ptId,
            esvyAttcId = this.esvyAttcId,
            rcptPhc = this.rcptPhc,
            telno = this.telno,
            addr = this.addr,
            mpno = this.mpno,
            nokNm = this.nokNm,
            diagNm = this.diagNm,
            diagGrde = this.diagGrde,
            job = this.job,
            cv19Symp = this.cv19Symp,
            occrDt = this.occrDt,
            diagDt = this.diagDt,
            rptDt = this.rptDt,
            dfdgExamRslt = this.dfdgExamRslt,
            ptCatg = this.ptCatg,
            admsYn = this.admsYn,
            dethYn = this.dethYn,
            rptType = this.rptType,
            rmk = this.rmk,
            instNm = this.instNm,
            instId = this.instId,
            instTelno = this.instTelno,
            instAddr = this.instAddr,
            diagDrNm = this.diagDrNm,
            rptChfNm = this.rptChfNm,
            instBascAddr = this.instBascAddr,
            instDetlAddr = this.instDetlAddr,
            instZip = this.instZip,
            diagAttcId = this.diagAttcId,
        )
    }

    fun saveInfoPt(infoPt: InfoPt) {
        this.telno = infoPt.telno
        this.addr = infoPt.addr
        this.mpno = infoPt.mpno
        this.nokNm = infoPt.nokNm
        this.job = infoPt.job
        this.dethYn = infoPt.dethYn
        this.esvyAttcId = infoPt.attcId
    }
}