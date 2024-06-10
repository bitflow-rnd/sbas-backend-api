package org.sbas.entities.svrt

import jakarta.persistence.*
import org.sbas.entities.CommonEntity
import java.io.Serializable

/**
 * 중증 환자 정보
 */
@Entity
@Table(name = "svrt_pt")
class SvrtPt(
    @EmbeddedId
    var id: SvrtPtId? = null,

    @Column(name = "pid", nullable = false, length = 10)
    var pid: String? = null, // 병원 PID

    @Column(name = "mon_strt_dt", nullable = false, length = 8)
    var monStrtDt: String? = null, // 관찰 시작 날짜

    @Column(name = "mon_strt_tm", nullable = false, length = 6)
    var monStrtTm: String? = null, // 관찰 시작 시간

    @Column(name = "mon_end_dt", length = 8)
    var monEndDt: String? = null, // 관찰 종료 날짜

    @Column(name = "mon_end_tm", length = 6)
    var monEndTm: String? = null, // 관찰 종료 시간

) : CommonEntity()

@Embeddable
data class SvrtPtId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null, // 환자 ID

    @Column(name = "hosp_id", nullable = false, length = 10)
    var hospId: String? = null, // 병원 ID

    @Column(name = "rgst_seq", nullable = false)
    var rgstSeq: Int? = null, // 등록 순번

) : Serializable {

    companion object {
        private const val serialVersionUID = -8539487836216536155L
    }
}