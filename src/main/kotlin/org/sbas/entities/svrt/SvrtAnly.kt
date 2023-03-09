package org.sbas.entities.svrt

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import org.sbas.serializers.BigDecimalSerializer
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Serializable
@Entity
@Table(name = "svrt_anly")
class SvrtAnly(
    @EmbeddedId
    var id: SvrtAnlyId? = null,

    @Column(name = "ipt_type_cd", nullable = false, length = 8)
    var iptTypeCd: String? = null,

    @Column(name = "send_dt", nullable = false, length = 8)
    var sendDt: String? = null,

    @Column(name = "send_tm", nullable = false, length = 6)
    var sendTm: String? = null,

    @Column(name = "news_resp", precision = 3)
    @Serializable(with = BigDecimalSerializer::class)
    var newsResp: BigDecimal? = null,

    @Column(name = "news_sbp", precision = 3)
    @Serializable(with = BigDecimalSerializer::class)
    var newsSbp: BigDecimal? = null,

    @Column(name = "news_hr", precision = 3)
    @Serializable(with = BigDecimalSerializer::class)
    var newsHr: BigDecimal? = null,

    @Column(name = "news_spo2", precision = 3)
    @Serializable(with = BigDecimalSerializer::class)
    var newsSpo2: BigDecimal? = null,

    @Column(name = "news_oxy_yn", length = 1)
    var newsOxyYn: String? = null,

    @Column(name = "news_bdtp", precision = 3, scale = 1)
    @Serializable(with = BigDecimalSerializer::class)
    var newsBdtp: BigDecimal? = null,

    @Column(name = "news_avpu", length = 1)
    var newsAvpu: String? = null,

    @Column(name = "news_score_curr", precision = 1)
    @Serializable(with = BigDecimalSerializer::class)
    var newsScoreCurr: BigDecimal? = null,

    @Column(name = "news_score_prdt", precision = 1)
    @Serializable(with = BigDecimalSerializer::class)
    var newsScorePrdt: BigDecimal? = null,

    @Column(name = "who_nasal_yn", length = 1)
    var whoNasalYn: String? = null,

    @Column(name = "who_niv_yn", length = 1)
    var whoNivYn: String? = null,

    @Column(name = "who_iv_yn", length = 1)
    var whoIvYn: String? = null,

    @Column(name = "who_fio2", precision = 3)
    @Serializable(with = BigDecimalSerializer::class)
    var whoFio2: BigDecimal? = null,

    @Column(name = "who_spo2", precision = 3)
    @Serializable(with = BigDecimalSerializer::class)
    var whoSpo2: BigDecimal? = null,

    @Column(name = "who_dial_yn", length = 1)
    var whoDialYn: String? = null,

    @Column(name = "who_ecmo_yn", length = 1)
    var whoEcmoYn: String? = null,

    @Column(name = "who_score_curr", precision = 1)
    @Serializable(with = BigDecimalSerializer::class)
    var whoScoreCurr: BigDecimal? = null,

    @Column(name = "who_score_prdt", precision = 1)
    @Serializable(with = BigDecimalSerializer::class)
    var whoScorePrdt: BigDecimal? = null,

    @Column(name = "sbed_cd_curr", length = 8)
    var sbedCdCurr: String? = null,

    @Column(name = "sbed_cd_prdt", length = 8)
    var sbedCdPrdt: String? = null,

) : CommonEntity(), java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = 4103641062407301116L
    }
}