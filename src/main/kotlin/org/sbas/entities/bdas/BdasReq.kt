package org.sbas.entities.bdas

import org.sbas.entities.CommonEntity
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "bdas_req")
class BdasReq(
    @EmbeddedId
    var id: BdasReqId? = null,

    @Column(name = "req_dt", length = 8)
    var reqDt: String? = null,

    @Column(name = "req_tm", length = 6)
    var reqTm: String? = null,

    @Column(name = "req_type_cd", length = 8)
    var reqTypeCd: String? = null,

    @Column(name = "req_hosp_id", length = 10)
    var reqHospId: String? = null,

    @Column(name = "req_user_id", length = 10)
    var reqUserId: String? = null,

    @Column(name = "pt_loc_addr", length = 100)
    var ptLocAddr: String? = null,
    @Column(name = "pt_loc_lat", length = 20)
    var ptLocLat: String? = null,

    @Column(name = "pt_loc_lon", length = 20)
    var ptLocLon: String? = null,

    @Column(name = "adms_yn", length = 1)
    var admsYn: String? = null,

    @Column(name = "clnc_yn", length = 1)
    var clncYn: String? = null,

    @Column(name = "nok_nm", length = 10)
    var nokNm: String? = null,

    @Column(name = "nok_telno", length = 11)
    var nokTelno: String? = null,

    @Column(name = "adms_hosp_id", length = 10)
    var admsHospId: String? = null,

    @Column(name = "adms_dept_nm", length = 20)
    var admsDeptNm: String? = null,

    @Column(name = "adms_spcl_nm", length = 10)
    var admsSpclNm: String? = null,

    @Column(name = "adms_spcl_telno", length = 11)
    var admsSpclTelno: String? = null,

    @Column(name = "pt_type_cd", length = 8)
    var ptTypeCd: String? = null,

    @Column(name = "diag_cd", length = 8)
    var diagCd: String? = null,

    @Column(name = "diag_nm_hng", length = 100)
    var diagNmHng: String? = null,

    @Column(name = "diag_nm_eng", length = 100)
    var diagNmEng: String? = null,

    @Column(name = "svrt_type_cd", length = 8)
    var svrtTypeCd: String? = null,

    @Column(name = "news_score_curr", precision = 1)
    var newsScoreCurr: BigDecimal? = null,

    @Column(name = "news_score_prdt", precision = 1)
    var newsScorePrdt: BigDecimal? = null,

    @Column(name = "who_score_curr", precision = 1)
    var whoScoreCurr: BigDecimal? = null,

    @Column(name = "who_score_prdt", precision = 1)
    var whoScorePrdt: BigDecimal? = null,

    @Column(name = "sbed_cd_curr", length = 8)
    var sbedCdCurr: String? = null,

    @Column(name = "sbed_cd_prdt", length = 8)
    var sbedCdPrdt: String? = null,

    @Column(name = "\"desc\"", length = 4000)
    var desc: String? = null,

    @Column(name = "attc_id", length = 10)
    var attcId: String? = null,
) : CommonEntity(), java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = 4750784331506783549L
    }
}