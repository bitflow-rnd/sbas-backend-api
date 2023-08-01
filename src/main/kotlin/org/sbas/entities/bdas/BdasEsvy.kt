package org.sbas.entities.bdas

import org.hibernate.annotations.DynamicUpdate
import org.sbas.entities.CommonEntity
import java.io.Serializable
import javax.persistence.*

/**
 * 역학조사서 정보
 */
@DynamicUpdate
@Entity
@Table(name = "bdas_esvy")
class BdasEsvy(
    @Id
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String, // 환자 ID

    @Column(name = "esvy_attc_id")
    var esvyAttcId: String? = null, // 역학조사서 첨부 ID

    @Column(name = "rcpt_phc", length = 50)
    var rcptPhc: String? = null, // 수신 보건소

    @Column(name = "diag_nm", length = 100)
    var diagNm: String? = null, // 질병 이름

    @Column(name = "diag_grde", length = 10)
    var diagGrde: String? = null, // 질병급

    @Column(name = "cv19_symp", length = 50)
    var cv19Symp: String? = null, // 코로나19 증상

    @Column(name = "occr_dt", length = 8)
    var occrDt: String? = null, // 발병 날짜

    @Column(name = "diag_dt", length = 8)
    var diagDt: String? = null, // 진단 날짜

    @Column(name = "rpt_dt", length = 8)
    var rptDt: String? = null, // 신고 날짜

    @Column(name = "dfdg_exam_rslt", length = 10)
    var dfdgExamRslt: String? = null, // 확진 검사 결과

    @Column(name = "pt_catg", length = 10)
    var ptCatg: String? = null, // 환자 등 분류

    @Column(name = "adms_yn", length = 10)
    var admsYn: String? = null, // 입원 여부

    @Column(name = "rpt_type", length = 10)
    var rptType: String? = null, // 신고 구분

    @Column(name = "rmk", length = 100)
    var rmk: String? = null, // 비고

    @Column(name = "inst_nm", length = 50)
    var instNm: String? = null, // 요양기관 이름

    @Column(name = "inst_id", length = 20)
    var instId: String? = null, // 요양기관 기호

    @Column(name = "inst_telno", length = 11)
    var instTelno: String? = null, // 요양기관 전화번호

    @Column(name = "inst_addr", length = 100)
    var instAddr: String? = null, // 요양기관 주소

    @Column(name = "diag_dr_nm", length = 10)
    var diagDrNm: String? = null, // 진단 의사 이름

    @Column(name = "rpt_chf_nm", length = 10)
    var rptChfNm: String? = null, // 신고 기관장 성명

    @Column(name = "diag_attc_id", length = 12)
    var diagAttcId: String? = null, // 진단 정보 첨부 ID
) : CommonEntity(), Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "bdas_seq", nullable = false)
    var bdasSeq: Int = 0 // 병상 배정 순번
        protected set

    companion object {
        private const val serialVersionUID = 2286635814191011763L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BdasEsvy) return false

        if (ptId != other.ptId) return false
        if (bdasSeq != other.bdasSeq) return false
        if (esvyAttcId != other.esvyAttcId) return false
        if (rcptPhc != other.rcptPhc) return false
        if (diagNm != other.diagNm) return false
        if (diagGrde != other.diagGrde) return false
        if (cv19Symp != other.cv19Symp) return false
        if (occrDt != other.occrDt) return false
        if (diagDt != other.diagDt) return false
        if (rptDt != other.rptDt) return false
        if (dfdgExamRslt != other.dfdgExamRslt) return false
        if (ptCatg != other.ptCatg) return false
        if (admsYn != other.admsYn) return false
        if (rptType != other.rptType) return false
        if (rmk != other.rmk) return false
        if (instNm != other.instNm) return false
        if (instId != other.instId) return false
        if (instTelno != other.instTelno) return false
        if (instAddr != other.instAddr) return false
        if (diagDrNm != other.diagDrNm) return false
        if (rptChfNm != other.rptChfNm) return false
        return diagAttcId == other.diagAttcId
    }

    override fun hashCode(): Int {
        var result = ptId.hashCode()
        result = 31 * result + (bdasSeq)
        result = 31 * result + (esvyAttcId?.hashCode() ?: 0)
        result = 31 * result + (rcptPhc?.hashCode() ?: 0)
        result = 31 * result + (diagNm?.hashCode() ?: 0)
        result = 31 * result + (diagGrde?.hashCode() ?: 0)
        result = 31 * result + (cv19Symp?.hashCode() ?: 0)
        result = 31 * result + (occrDt?.hashCode() ?: 0)
        result = 31 * result + (diagDt?.hashCode() ?: 0)
        result = 31 * result + (rptDt?.hashCode() ?: 0)
        result = 31 * result + (dfdgExamRslt?.hashCode() ?: 0)
        result = 31 * result + (ptCatg?.hashCode() ?: 0)
        result = 31 * result + (admsYn?.hashCode() ?: 0)
        result = 31 * result + (rptType?.hashCode() ?: 0)
        result = 31 * result + (rmk?.hashCode() ?: 0)
        result = 31 * result + (instNm?.hashCode() ?: 0)
        result = 31 * result + (instId?.hashCode() ?: 0)
        result = 31 * result + (instTelno?.hashCode() ?: 0)
        result = 31 * result + (instAddr?.hashCode() ?: 0)
        result = 31 * result + (diagDrNm?.hashCode() ?: 0)
        result = 31 * result + (rptChfNm?.hashCode() ?: 0)
        result = 31 * result + (diagAttcId?.hashCode() ?: 0)
        return result
    }

}