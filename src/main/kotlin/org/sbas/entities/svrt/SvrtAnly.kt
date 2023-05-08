package org.sbas.entities.svrt

import org.sbas.entities.CommonEntity
import java.io.Serializable
import javax.persistence.*

/**
 * 중증 분석 결과
 */
@Entity
@Table(name = "svrt_anly")
class SvrtAnly(
    @EmbeddedId
    var id: SvrtAnlyId? = null,

    @Column(name = "pid", nullable = false, length = 10)
    var pid: String? = null, // 병원 PID

    @Column(name = "coll_dt", nullable = false, length = 8)
    var collDt: String? = null, // 수집 날짜

    @Column(name = "coll_tm", nullable = false, length = 6)
    var collTm: String? = null, // 수집 시간

    @Column(name = "anly_tm", nullable = false, length = 6)
    var anlyTm  : String? = null, // 분석 시간

    @Column(name = "prdt_dt", nullable = false, length = 8)
    var prdtDt  : String? = null, // 예측 날짜

    @Column(name = "svrt_prob", nullable = false, length = 6)
    var svrtProb: String? = null, // 중증 확률

    @Column(name = "rslt_lbl", nullable = false, length = 100)
    var rsltLbl: String? = null, // 결과 라벨
) : CommonEntity()

@Embeddable
data class SvrtAnlyId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null, // 환자 ID

    @Column(name = "hosp_id", nullable = false, length = 10)
    var hospId: String? = null, // 병원 ID

    @Column(name = "rgst_seq", nullable = false)
    var rgstSeq: Int? = null, // 등록 순번

    @Column(name = "msre_dt", nullable = false, length = 8)
    var msreDt: String? = null, // 측정 날짜

    @Column(name = "coll_seq", nullable = false)
    var collSeq : Int? = null, // 수집 순번

    @Column(name = "anly_dt", nullable = false, length = 8)
    var anlyDt  : String? = null, // 분석 날짜

    @Column(name = "anly_seq", nullable = false)
    var anlySeq : Int? = null, // 분석 순번
) : Serializable {

    companion object {
        private const val serialVersionUID = 4678885074474623613L
    }
}