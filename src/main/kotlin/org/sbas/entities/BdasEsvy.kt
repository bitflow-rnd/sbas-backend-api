package org.sbas.entities

import java.math.BigDecimal
import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "bdas_esvy")
open class BdasEsvy {
    @EmbeddedId
    open var id: BdasEsvyId? = null

    @Column(name = "hist_cd", nullable = false, length = 8)
    open var histCd: String? = null

    @Column(name = "esvy_seq", precision = 10)
    open var esvySeq: BigDecimal? = null

    @Column(name = "esvy_imsg")
    open var esvyImsg: ByteArray? = null

    @Column(name = "rcpt_phc", length = 50)
    open var rcptPhc: String? = null

    @Column(name = "telno", length = 11)
    open var telno: String? = null

    @Column(name = "addr", length = 100)
    open var addr: String? = null

    @Column(name = "mpno", length = 11)
    open var mpno: String? = null

    @Column(name = "nok_nm", length = 10)
    open var nokNm: String? = null

    @Column(name = "diag_nm", length = 100)
    open var diagNm: String? = null

    @Column(name = "diag_grde", length = 10)
    open var diagGrde: String? = null

    @Column(name = "job", length = 20)
    open var job: String? = null

    @Column(name = "cv19_symp", length = 50)
    open var cv19Symp: String? = null

    @Column(name = "occr_dt", length = 8)
    open var occrDt: String? = null

    @Column(name = "diag_dt", length = 8)
    open var diagDt: String? = null

    @Column(name = "rpt_dt", length = 8)
    open var rptDt: String? = null

    @Column(name = "dfdg_exam_rslt", length = 10)
    open var dfdgExamRslt: String? = null

    @Column(name = "pt_catg", length = 10)
    open var ptCatg: String? = null

    @Column(name = "adms_yn", length = 10)
    open var admsYn: String? = null

    @Column(name = "deth_yn", length = 10)
    open var dethYn: String? = null

    @Column(name = "rpt_type", length = 10)
    open var rptType: String? = null

    @Column(name = "rmk", length = 100)
    open var rmk: String? = null

    @Column(name = "inst_nm", length = 50)
    open var instNm: String? = null

    @Column(name = "inst_id", length = 20)
    open var instId: String? = null

    @Column(name = "inst_telno", length = 11)
    open var instTelno: String? = null

    @Column(name = "inst_addr", length = 100)
    open var instAddr: String? = null

    @Column(name = "diag_dr_nm", length = 10)
    open var diagDrNm: String? = null

    @Column(name = "rpt_chf_nm", length = 10)
    open var rptChfNm: String? = null

    @Column(name = "rgst_user_id", nullable = false, length = 10)
    open var rgstUserId: String? = null

    @Column(name = "rgst_dttm", nullable = false)
    open var rgstDttm: Instant? = null

    @Column(name = "updt_user_id", nullable = false, length = 10)
    open var updtUserId: String? = null

    @Column(name = "updt_dttm", nullable = false)
    open var updtDttm: Instant? = null
}