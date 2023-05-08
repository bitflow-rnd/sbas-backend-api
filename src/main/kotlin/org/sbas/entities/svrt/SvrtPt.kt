package org.sbas.entities.svrt

import org.sbas.entities.CommonEntity
import java.io.Serializable
import javax.persistence.*

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

    @Column(name = "rec_strt_dt", nullable = false, length = 8)
    var recStrtDt: String? = null, // 관찰 시작 날짜

    @Column(name = "rec_strt_tm", nullable = false, length = 6)
    var recStrtTm: String? = null, // 관찰 시작 시간

    @Column(name = "rec_end_dt", nullable = false, length = 8)
    var recEndDt: String? = null, // 관찰 종료 날짜

    @Column(name = "rec_end_tm", nullable = false, length = 6)
    var recEndTm: String? = null, // 관찰 종료 시간

    @Column(name = "dept_nm", length = 20)
    var deptNm: String? = null, // 진료과 이름

    @Column(name = "ward_nm", length = 20)
    var wardNm: String? = null, // 병동 이름

    @Column(name = "room_nm", length = 20)
    var roomNm: String? = null, // 병실 이름

    @Column(name = "spcl_id", length = 15)
    var spclId: String? = null, // 담당의 ID

    @Column(name = "spcl_nm", nullable = false, length = 10)
    var spclNm: String? = null, // 담당의 이름

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