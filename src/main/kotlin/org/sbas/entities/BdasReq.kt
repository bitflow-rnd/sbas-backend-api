package org.sbas.entities

import java.math.BigDecimal
import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "bdas_req")
open class BdasReq {
    @EmbeddedId
    open var id: BdasReqId? = null

    @Column(name = "req_dt", length = 8)
    open var reqDt: String? = null

    @Column(name = "req_tm", length = 6)
    open var reqTm: String? = null

    @Column(name = "req_type_cd", length = 8)
    open var reqTypeCd: String? = null

    @Column(name = "req_hosp_id", length = 10)
    open var reqHospId: String? = null

    @Column(name = "req_user_id", length = 10)
    open var reqUserId: String? = null

    @Column(name = "pt_loc_addr", length = 100)
    open var ptLocAddr: String? = null

    @Column(name = "pt_loc_lat", length = 20)
    open var ptLocLat: String? = null

    @Column(name = "pt_loc_lon", length = 20)
    open var ptLocLon: String? = null

    @Column(name = "adms_yn", length = 1)
    open var admsYn: String? = null

    @Column(name = "clnc_yn", length = 1)
    open var clncYn: String? = null

    @Column(name = "nok_nm", length = 10)
    open var nokNm: String? = null

    @Column(name = "nok_telno", length = 11)
    open var nokTelno: String? = null

    @Column(name = "adms_hosp_id", length = 10)
    open var admsHospId: String? = null

    @Column(name = "adms_dept_nm", length = 20)
    open var admsDeptNm: String? = null

    @Column(name = "adms_spcl_nm", length = 10)
    open var admsSpclNm: String? = null

    @Column(name = "adms_spcl_telno", length = 11)
    open var admsSpclTelno: String? = null

    @Column(name = "pt_type_cd", length = 8)
    open var ptTypeCd: String? = null

    @Column(name = "diag_cd", length = 8)
    open var diagCd: String? = null

    @Column(name = "diag_nm_hng", length = 100)
    open var diagNmHng: String? = null

    @Column(name = "diag_nm_eng", length = 100)
    open var diagNmEng: String? = null

    @Column(name = "svrt_type_cd", length = 8)
    open var svrtTypeCd: String? = null

    @Column(name = "news_score_curr", precision = 1)
    open var newsScoreCurr: BigDecimal? = null

    @Column(name = "news_score_prdt", precision = 1)
    open var newsScorePrdt: BigDecimal? = null

    @Column(name = "who_score_curr", precision = 1)
    open var whoScoreCurr: BigDecimal? = null

    @Column(name = "who_score_prdt", precision = 1)
    open var whoScorePrdt: BigDecimal? = null

    @Column(name = "sbed_cd_curr", length = 8)
    open var sbedCdCurr: String? = null

    @Column(name = "sbed_cd_prdt", length = 8)
    open var sbedCdPrdt: String? = null

    @Column(name = "\"desc\"", length = 4000)
    open var desc: String? = null

    @Column(name = "attc_id", length = 10)
    open var attcId: String? = null

    @Column(name = "rgst_user_id", nullable = false, length = 10)
    open var rgstUserId: String? = null

    @Column(name = "rgst_dttm", nullable = false)
    open var rgstDttm: Instant? = null

    @Column(name = "updt_user_id", nullable = false, length = 10)
    open var updtUserId: String? = null

    @Column(name = "updt_dttm", nullable = false)
    open var updtDttm: Instant? = null
}