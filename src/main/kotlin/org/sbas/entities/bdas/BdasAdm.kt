package org.sbas.entities.bdas

import org.sbas.entities.CommonEntity
import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.*

/**
 * 입원 정보
 */
@Entity
@Table(name = "bdas_adms")
class BdasAdm(
    @EmbeddedId
    var id: BdasAdmId? = null,

    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null, // 이력 코드

    @Column(name = "hosp_id", nullable = false, length = 10)
    var hospId: String? = null, // 병원 ID

    @Column(name = "dept_nm", length = 20)
    var deptNm: String? = null, // 진료과 이름

    @Column(name = "ward_nm", length = 20)
    var wardNm: String? = null, // 병동 이름

    @Column(name = "room_nm", length = 20)
    var roomNm: String? = null, // 병실 이름

    @Column(name = "adms_dt", nullable = false, length = 8)
    var admsDt: String? = null, // 입원 날짜

    @Column(name = "adms_tm", nullable = false, length = 6)
    var admsTm: String? = null, // 입원 시간

    @Column(name = "dsch_dt", length = 8)
    var dschDt: String? = null, // 퇴원 날짜

    @Column(name = "dsch_tm", length = 6)
    var dschTm: String? = null, // 퇴원 시간

    @Column(name = "dsch_rsn_cd", length = 8)
    var dschRsnCd: String? = null, // 퇴원 사유 코드

    @Column(name = "dsch_rsn_detl", length = 500)
    var dschRsnDetl: String? = null, // 퇴원 사유 상세
) : CommonEntity()

@Embeddable
data class BdasAdmId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null, // 환자 ID

    @Column(name = "bdas_seq", nullable = false, precision = 10)
    var bdasSeq: BigDecimal? = null, // 병상 배정 순번

    @Column(name = "hist_seq", nullable = false, precision = 3)
    var histSeq: BigDecimal? = null, // 이력 순번
) : Serializable {

    companion object {
        private const val serialVersionUID = -1724344466522266093L
    }
}


