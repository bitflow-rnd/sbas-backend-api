package org.sbas.entities.svrt

import org.sbas.entities.CommonEntity
import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.*

/**
 * 중증 환자 정보
 */
@Entity
@Table(name = "svrt_pt")
class SvrtPt(
    @EmbeddedId
    var id: SvrtPtId? = null,

    @Column(name = "hosp_pid", nullable = false, length = 10)
    var hospPid: String? = null, // 병원 ID

    @Column(name = "mon_strt_dt", nullable = false, length = 8)
    var monStrtDt: String? = null, // 관찰 시작 날짜

    @Column(name = "mon_end_dt", nullable = false, length = 8)
    var monEndDt: String? = null, // 관찰 종료 날짜

    @Column(name = "pt_type_cd", nullable = false, length = 10)
    var ptTypeCd: String? = null, // 환자 유형 코드

    @Column(name = "svrt_type_cd", length = 10)
    var svrtTypeCd: String? = null, // 중증 유형 코드

    @Column(name = "dept_nm", length = 20)
    var deptNm: String? = null, // 진료과 이름

    @Column(name = "ward_nm", length = 20)
    var wardNm: String? = null, // 병동 이름

    @Column(name = "room_nm", length = 20)
    var roomNm: String? = null, // 병실 이름

    @Column(name = "spcl_nm", nullable = false, length = 10)
    var spclNm: String? = null, // 담당의 이름

    @Column(name = "spcl_telno", length = 11)
    var spclTelno: String? = null, // 담당의 전화번호

) : CommonEntity()

@Embeddable
data class SvrtPtId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null, // 환자 ID

    @Column(name = "hosp_id", nullable = false, length = 10)
    var hospId: String? = null, // 병원 ID

    @Column(name = "rgst_seq", nullable = false, precision = 10)
    var rgstSeq: BigDecimal? = null, // 등록 순번

) : Serializable {

    companion object {
        private const val serialVersionUID = -8539487836216536155L
    }
}