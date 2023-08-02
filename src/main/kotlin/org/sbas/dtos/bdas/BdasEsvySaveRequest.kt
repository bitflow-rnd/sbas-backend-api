package org.sbas.dtos.bdas

import org.sbas.entities.bdas.BdasEsvy
import org.sbas.entities.info.InfoPt
import javax.validation.constraints.NotBlank

data class BdasEsvySaveRequest(
    @field: NotBlank
    var ptId: String,
    var esvyAttcId: String? = null,

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
    var diagAttcId: String?,
) {
    fun toEntity(): BdasEsvy {
        return BdasEsvy(
            ptId = this.ptId,
            esvyAttcId = this.esvyAttcId,
            rcptPhc = this.rcptPhc,
            diagNm = this.diagNm,
            diagGrde = this.diagGrde,
            cv19Symp = this.cv19Symp,
            occrDt = this.occrDt,
            diagDt = this.diagDt,
            rptDt = this.rptDt,
            dfdgExamRslt = this.dfdgExamRslt,
            ptCatg = this.ptCatg,
            admsYn = this.admsYn,
            rptType = this.rptType,
            rmk = this.rmk,
            instNm = this.instNm,
            instId = this.instId,
            instTelno = this.instTelno,
            instAddr = this.instAddr,
            diagDrNm = this.diagDrNm,
            rptChfNm = this.rptChfNm,
            diagAttcId = this.diagAttcId,
        )
    }

    fun saveInfoPt(infoPt: InfoPt) {
        this.esvyAttcId = infoPt.attcId
    }
}