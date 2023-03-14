package org.sbas.entities.bdas

import org.sbas.entities.CommonEntity
import java.math.BigDecimal
import javax.persistence.*

/**
 * 이송 정보
 */
@Entity
@Table(name = "bdas_trns")
class BdasTrn(
    @EmbeddedId
    var id: BdasTrnId? = null,

    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null, // 이력 코드

    @Column(name = "inst_id", nullable = false, length = 10)
    var instId: String? = null, // 기관 ID

    @Column(name = "crew_id_1", nullable = false, length = 10)
    var crewId1: String? = null, // 구급대원 ID (교)

    @Column(name = "crew_id_2", length = 10)
    var crewId2: String? = null, // 구급대원 ID (사)

    @Column(name = "dprt_hosp_id", length = 10)
    var dprtHospId: String? = null, // 출발 병원 ID

    @Column(name = "dprt_dt", nullable = false, length = 8)
    var dprtDt: String? = null, // 출발 날짜

    @Column(name = "dprt_tm", nullable = false, length = 6)
    var dprtTm: String? = null, // 출발 시간

    @Column(name = "arvl_hosp_id", nullable = false, length = 10)
    var arvlHospId: String? = null, // 도착 병원 ID

    @Column(name = "arvl_dt", nullable = false, length = 8)
    var arvlDt: String? = null, // 도착 날짜

    @Column(name = "arvl_tm", nullable = false, length = 6)
    var arvlTm: String? = null, // 도착 시간

    @Column(name = "trns_stat_cd", nullable = false, length = 8)
    var trnsStatCd: String? = null, // 이송 상태 코드
) : CommonEntity(), java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = 44633960065844049L
    }
}

@Embeddable
data class BdasTrnId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null, // 환자 ID

    @Column(name = "bdas_seq", nullable = false, precision = 10)
    var bdasSeq: BigDecimal? = null, // 병상 배정 순번

    @Column(name = "hist_seq", nullable = false, precision = 3)
    var histSeq: BigDecimal? = null, // 이력 순번
) : java.io.Serializable {

    companion object {
        private const val serialVersionUID = -3748060460236523127L
    }
}