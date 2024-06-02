package org.sbas.entities.bdas

import jakarta.persistence.*
import org.sbas.entities.CommonEntity
import java.io.Serializable

/**
 * 입원 정보
 */
@Entity
@Table(name = "bdas_adms")
class BdasAdms(
    @EmbeddedId
    var id: BdasAdmsId,

    @Column(name = "hosp_id", nullable = false, length = 10)
    var hospId: String, // 병원 ID

    @Column(name = "dept_nm", length = 20)
    var deptNm: String? = null, // 진료과 이름

    @Column(name = "room_nm", length = 20)
    var roomNm: String? = null, // 병실 이름

    @Column(name = "spcl_id", length = 15)
    var spclId: String? = null, // 담당의 ID

    @Column(name = "spcl_nm", length = 10)
    var spclNm: String? = null, // 담당의 이름

    @Column(name = "chrg_telno", length = 14)
    var chrgTelno: String? = null, // 담당 전화번호

    @Column(name = "adms_dt", length = 8)
    var admsDt: String? = null, // 입원 날짜

    @Column(name = "adms_tm", length = 6)
    var admsTm: String? = null, // 입원 시간

    @Column(name = "dsch_dt", length = 8)
    var dschDt: String? = null, // 퇴원 날짜

    @Column(name = "dsch_tm", length = 6)
    var dschTm: String? = null, // 퇴원 시간

    @Column(name = "dsch_rsn_cd", length = 8)
    var dschRsnCd: String? = null, // 퇴원 사유 코드

    @Column(name = "msg", length = 500)
    var msg: String? = null, // 퇴원 사유 상세
    
    @Column(name = "adms_stat_cd", nullable = false, length = 8)
    var admsStatCd: String, // 입퇴원상태 코드
) : CommonEntity() {

    fun isAdmsStatCdDuplicate(admsStatCd: String): Boolean {
        return this.admsStatCd == admsStatCd
    }
}

@Embeddable
data class BdasAdmsId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String, // 환자 ID

    @Column(name = "bdas_seq", nullable = false)
    var bdasSeq: Int, // 병상 배정 순번

    @Column(name = "adms_seq", nullable = false)
    var admsSeq: Int = 1,
) : Serializable {

    companion object {
        private const val serialVersionUID = -1724344466522266093L
    }
}


