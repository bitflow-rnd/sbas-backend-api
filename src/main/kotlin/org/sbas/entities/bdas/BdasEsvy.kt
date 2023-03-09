package org.sbas.entities.bdas

import org.sbas.entities.CommonEntity
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "bdas_esvy")
class BdasEsvy(
    @EmbeddedId
    var id: BdasEsvyId? = null,

    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null,

    @Column(name = "esvy_seq", precision = 10)
    var esvySeq: BigDecimal? = null,

    @Column(name = "esvy_imsg")
    var esvyImsg: ByteArray? = null,

    @Column(name = "rcpt_phc", length = 50)
    var rcptPhc: String? = null,

    @Column(name = "telno", length = 11)
    var telno: String? = null,

    @Column(name = "addr", length = 100)
    var addr: String? = null,

    @Column(name = "mpno", length = 11)
    var mpno: String? = null,

    @Column(name = "nok_nm", length = 10)
    var nokNm: String? = null,

    @Column(name = "diag_nm", length = 100)
    var diagNm: String? = null,

    @Column(name = "diag_grde", length = 10)
    var diagGrde: String? = null,

    @Column(name = "job", length = 20)
    var job: String? = null,

    @Column(name = "cv19_symp", length = 50)
    var cv19Symp: String? = null,

    @Column(name = "occr_dt", length = 8)
    var occrDt: String? = null,

    @Column(name = "diag_dt", length = 8)
    var diagDt: String? = null,

    @Column(name = "rpt_dt", length = 8)
    var rptDt: String? = null,

    @Column(name = "dfdg_exam_rslt", length = 10)
    var dfdgExamRslt: String? = null,

    @Column(name = "pt_catg", length = 10)
    var ptCatg: String? = null,

    @Column(name = "adms_yn", length = 10)
    var admsYn: String? = null,

    @Column(name = "deth_yn", length = 10)
    var dethYn: String? = null,

    @Column(name = "rpt_type", length = 10)
    var rptType: String? = null,

    @Column(name = "rmk", length = 100)
    var rmk: String? = null,

    @Column(name = "inst_nm", length = 50)
    var instNm: String? = null,

    @Column(name = "inst_id", length = 20)
    var instId: String? = null,

    @Column(name = "inst_telno", length = 11)
    var instTelno: String? = null,

    @Column(name = "inst_addr", length = 100)
    var instAddr: String? = null,

    @Column(name = "diag_dr_nm", length = 10)
    var diagDrNm: String? = null,

    @Column(name = "rpt_chf_nm", length = 10)
    var rptChfNm: String? = null,
) : CommonEntity(), java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = -2091171034347778229L
    }
}