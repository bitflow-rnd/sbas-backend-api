package org.sbas.entities.bdas

import org.hibernate.annotations.DynamicUpdate
import org.sbas.entities.CommonEntity
import java.io.Serializable
import jakarta.persistence.*

/**
 * 역학조사서 정보
 */
@DynamicUpdate
@Entity
@Table(name = "bdas_esvy")
class BdasEsvy(
    @Column(name = "pt_id", nullable = false, length = 10)
    val ptId: String, // 환자 ID

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bdas_seq", nullable = false)
    val bdasSeq: Int = 0 // 병상 배정 순번

    companion object {
        private const val serialVersionUID = 2286635814191011763L
    }
}
