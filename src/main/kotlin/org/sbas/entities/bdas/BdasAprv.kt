package org.sbas.entities.bdas

import org.sbas.entities.CommonEntity
import java.io.Serializable
import javax.persistence.*

/**
 * 병상 배정 승인 정보
 */
@Entity
@Table(name = "bdas_aprv")
class BdasAprv(
    @EmbeddedId
    var id: BdasAprvId? = null,

    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null, // 이력 코드

    @Column(name = "hosp_id", nullable = false, length = 10)
    var hospId: String? = null, // 병원 ID

    @Column(name = "room_nm", nullable = false, length = 20)
    var roomNm: String? = null, // 병실 이름

    @Column(name = "aprv_dt", nullable = false, length = 10)
    var aprvDt: String? = null, // 승인 날짜

    @Column(name = "aprv_tm", nullable = false, length = 10)
    var aprvTm: String? = null, // 승인 시간

    @Column(name = "aprv_yn", nullable = false, length = 8)
    var aprvYn: String? = null, // 승인 여부

    @Column(name = "neg_cd", length = 6)
    var negCd: String? = null, // 불가 코드

    @Column(name = "neg_detl", length = 1)
    var negDetl: String? = null, // 불가 상세
) : CommonEntity()

@Embeddable
data class BdasAprvId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null, // 환자 ID

    @Column(name = "bdas_seq", nullable = false, precision = 10)
    var bdasSeq: Int? = null, // 병상 배정 순번

    @Column(name = "hist_seq", nullable = false, precision = 3)
    var histSeq: Int? = null, // 이력 순번
) : Serializable {

    companion object {
        private const val serialVersionUID = -5290966609235076509L
    }
}