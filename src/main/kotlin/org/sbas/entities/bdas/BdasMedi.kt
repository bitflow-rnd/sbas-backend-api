package org.sbas.entities.bdas

import org.sbas.entities.CommonEntity
import java.math.BigDecimal
import javax.persistence.*

/**
 * 진료 확인 정보
 */
@Entity
@Table(name = "bdas_medi")
class BdasMedi(
    @EmbeddedId
    var id: BdasMediId? = null,

    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null, // 이력 코드

    @Column(name = "hosp_id", nullable = false, length = 10)
    var hospId: String? = null, // 병원 ID

    @Column(name = "dept_nm", nullable = false, length = 20)
    var deptNm: String? = null, // 진료과 이름

    @Column(name = "stff_id", nullable = false, length = 10)
    var stffId: String? = null, // 의료진 ID

    @Column(name = "stff_nm", nullable = false, length = 10)
    var stffNm: String? = null, // 의료진 이름

    @Column(name = "cnfm_dt", nullable = false, length = 8)
    var cnfmDt: String? = null, // 확인 날짜

    @Column(name = "cnfm_tm", nullable = false, length = 6)
    var cnfmTm: String? = null, // 확인 시간

    @Column(name = "aprv_yn", nullable = false, length = 1)
    var aprvYn: String? = null, // 승인 여부

    @Column(name = "neg_cd", length = 8)
    var negCd: String? = null, // 불가 코드

    @Column(name = "neg_detl", length = 500)
    var negDetl: String? = null, // 불가 상세
) : CommonEntity(), java.io.Serializable{

    companion object {
        private const val serialVersionUID: Long = -5775047080640955569L
    }
}

@Embeddable
data class BdasMediId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null, // 환자 ID

    @Column(name = "bdas_seq", nullable = false, precision = 10)
    var bdasSeq: BigDecimal? = null, // 병상 배정 순번

    @Column(name = "hist_seq", nullable = false, precision = 3)
    var histSeq: BigDecimal? = null, // 이력 순번
) : java.io.Serializable {

    companion object {
        private const val serialVersionUID = -5971773794245461589L
    }
}