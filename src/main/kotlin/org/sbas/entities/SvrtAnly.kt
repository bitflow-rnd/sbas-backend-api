package org.sbas.entities

import java.math.BigDecimal
import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "svrt_anly")
open class SvrtAnly {
    @EmbeddedId
    open var id: SvrtAnlyId? = null

    @Column(name = "ipt_type_cd", nullable = false, length = 8)
    open var iptTypeCd: String? = null

    @Column(name = "send_dt", nullable = false, length = 8)
    open var sendDt: String? = null

    @Column(name = "send_tm", nullable = false, length = 6)
    open var sendTm: String? = null

    @Column(name = "news_resp", precision = 3)
    open var newsResp: BigDecimal? = null

    @Column(name = "news_sbp", precision = 3)
    open var newsSbp: BigDecimal? = null

    @Column(name = "news_hr", precision = 3)
    open var newsHr: BigDecimal? = null

    @Column(name = "news_spo2", precision = 3)
    open var newsSpo2: BigDecimal? = null

    @Column(name = "news_oxy_yn", length = 1)
    open var newsOxyYn: String? = null

    @Column(name = "news_bdtp", precision = 3, scale = 1)
    open var newsBdtp: BigDecimal? = null

    @Column(name = "news_avpu", length = 1)
    open var newsAvpu: String? = null

    @Column(name = "news_score_curr", precision = 1)
    open var newsScoreCurr: BigDecimal? = null

    @Column(name = "news_score_prdt", precision = 1)
    open var newsScorePrdt: BigDecimal? = null

    @Column(name = "who_nasal_yn", length = 1)
    open var whoNasalYn: String? = null

    @Column(name = "who_niv_yn", length = 1)
    open var whoNivYn: String? = null

    @Column(name = "who_iv_yn", length = 1)
    open var whoIvYn: String? = null

    @Column(name = "who_fio2", precision = 3)
    open var whoFio2: BigDecimal? = null

    @Column(name = "who_spo2", precision = 3)
    open var whoSpo2: BigDecimal? = null

    @Column(name = "who_dial_yn", length = 1)
    open var whoDialYn: String? = null

    @Column(name = "who_ecmo_yn", length = 1)
    open var whoEcmoYn: String? = null

    @Column(name = "who_score_curr", precision = 1)
    open var whoScoreCurr: BigDecimal? = null

    @Column(name = "who_score_prdt", precision = 1)
    open var whoScorePrdt: BigDecimal? = null

    @Column(name = "sbed_cd_curr", length = 8)
    open var sbedCdCurr: String? = null

    @Column(name = "sbed_cd_prdt", length = 8)
    open var sbedCdPrdt: String? = null

    @Column(name = "rgst_user_id", nullable = false, length = 10)
    open var rgstUserId: String? = null

    @Column(name = "rgst_dttm", nullable = false)
    open var rgstDttm: Instant? = null

    @Column(name = "updt_user_id", nullable = false, length = 10)
    open var updtUserId: String? = null

    @Column(name = "updt_dttm", nullable = false)
    open var updtDttm: Instant? = null
}