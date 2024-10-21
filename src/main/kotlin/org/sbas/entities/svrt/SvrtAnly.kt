package org.sbas.entities.svrt

import jakarta.persistence.*
import org.sbas.entities.CommonEntity
import java.io.Serializable

/**
 * 중증 분석 결과
 */
@Entity
@Table(name = "svrt_anly")
class SvrtAnly(
  @EmbeddedId
  var id: SvrtAnlyId,

  @Column(name = "pid", nullable = false, length = 10)
  var pid: String, // 병원 PID

  @Column(name = "coll_dt", nullable = false, length = 8)
  var collDt: String, // 수집 날짜

  @Column(name = "coll_tm", nullable = false, length = 6)
  var collTm: String, // 수집 시간

  @Column(name = "anly_tm", nullable = false, length = 6)
  var anlyTm: String, // 분석 시간

  @Column(name = "prdt_dt", length = 8)
  var prdtDt: String? = null, // 예측 날짜

  @Column(name = "covsf", length = 20)
  var covSf: String? = null,

  ) : CommonEntity()

@Embeddable
data class SvrtAnlyId(
  @Column(name = "pt_id", nullable = false, length = 10)
  var ptId: String, // 환자 ID

  @Column(name = "hosp_id", nullable = false, length = 10)
  var hospId: String, // 병원 ID

  @Column(name = "rgst_seq", nullable = false)
  var rgstSeq: Int, // 등록 순번

  @Column(name = "msre_dt", nullable = false, length = 8)
  var msreDt: String, // 측정 날짜

  @Column(name = "coll_seq", nullable = false)
  var collSeq: Int, // 수집 순번

  @Column(name = "anly_dt", nullable = false, length = 8)
  var anlyDt: String, // 분석 날짜

  @Column(name = "anly_seq", nullable = false)
  var anlySeq: Int, // 분석 순번
) : Serializable {

  companion object {
    private const val serialVersionUID = 4678885074474623613L
  }
}