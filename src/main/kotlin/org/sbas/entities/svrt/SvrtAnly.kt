package org.sbas.entities.svrt

import org.sbas.entities.CommonEntity
import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.*

/**
 * 중증 분석 결과
 */
@Entity
@Table(name = "svrt_anly")
class SvrtAnly(
    @EmbeddedId
    var id: SvrtAnlyId? = null,

    @Column(name = "ipt_type_cd", nullable = false, length = 8)
    var iptTypeCd: String? = null, // 입력 유형 코드

    @Column(name = "send_dt", nullable = false, length = 8)
    var sendDt: String? = null, // 전송 날짜

    @Column(name = "send_tm", nullable = false, length = 6)
    var sendTm: String? = null, // 전송 시간

    @Column(name = "news_resp", precision = 3)
    var newsResp: BigDecimal? = null, // NEWS 호흡수

    @Column(name = "news_sbp", precision = 3)
    var newsSbp: BigDecimal? = null, // NEWS 수축기혈압

    @Column(name = "news_hr", precision = 3)
    var newsHr: BigDecimal? = null, // NEWS 맥박

    @Column(name = "news_spo2", precision = 3)
    var newsSpo2: BigDecimal? = null, // NEWS 산소포화도

    @Column(name = "news_oxy_yn", length = 1)
    var newsOxyYn: String? = null, // NEWS 산소투여 여부

    @Column(name = "news_bdtp", precision = 3, scale = 1)
    var newsBdtp: BigDecimal? = null, // NEWS 체온

    @Column(name = "news_avpu", length = 1)
    var newsAvpu: String? = null, // NEWS 의식수준

    @Column(name = "news_score_curr", precision = 1)
    var newsScoreCurr: BigDecimal? = null, // NEWS 점수 (현재)

    @Column(name = "news_score_prdt", precision = 1)
    var newsScorePrdt: BigDecimal? = null, // NEWS 점수 (예측)

    @Column(name = "who_nasal_yn", length = 1)
    var whoNasalYn: String? = null, // WHO 산소마스크 여부

    @Column(name = "who_niv_yn", length = 1)
    var whoNivYn: String? = null, // WHO NIV여부

    @Column(name = "who_iv_yn", length = 1)
    var whoIvYn: String? = null, // WHO IV여부

    @Column(name = "who_fio2", precision = 3)
    var whoFio2: BigDecimal? = null, // WHO 흡입산소농도

    @Column(name = "who_spo2", precision = 3)
    var whoSpo2: BigDecimal? = null, // WHO 산소포화도

    @Column(name = "who_dial_yn", length = 1)
    var whoDialYn: String? = null, // WHO 투석 여부

    @Column(name = "who_ecmo_yn", length = 1)
    var whoEcmoYn: String? = null, // WHO 체외막산소공급 여부

    @Column(name = "who_score_curr", precision = 1)
    var whoScoreCurr: BigDecimal? = null, // WHO 점수 (현재)

    @Column(name = "who_score_prdt", precision = 1)
    var whoScorePrdt: BigDecimal? = null, // WHO 점수 (예측)

    @Column(name = "sbed_cd_curr", length = 8)
    var sbedCdCurr: String? = null, // 중증병상 코드 (현재)

    @Column(name = "sbed_cd_prdt", length = 8)
    var sbedCdPrdt: String? = null, // 중증병상 코드 (예측)

) : CommonEntity()

@Embeddable
data class SvrtAnlyId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null, // 환자 ID

    @Column(name = "rgst_seq", nullable = false, precision = 10)
    var rgstSeq: BigDecimal? = null, // 등록 순번
) : Serializable {

    companion object {
        private const val serialVersionUID = 4678885074474623613L
    }
}