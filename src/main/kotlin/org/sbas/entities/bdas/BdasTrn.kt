package org.sbas.entities.bdas

import org.sbas.entities.CommonEntity
import java.io.Serializable
import javax.persistence.*

/**
 * 이송 정보
 */
@Entity
@Table(name = "bdas_trns")
class BdasTrns(
    @EmbeddedId
    var id: BdasTrnsId? = null,

    @Column(name = "inst_id", nullable = false, length = 10)
    var instId: String? = null, // 기관 ID

    @Column(name = "ambs_nm", nullable = false, length = 100)
    var ambsNm: String? = null,

    @Column(name = "crew_1_id", length = 15)
    var crew1Id : String? = null,

    @Column(name = "crew_1_pstn", nullable = false, length = 15)
    var crew1Pstn : String? = null,

    @Column(name = "crew_1_nm", nullable = false, length = 10)
    var crew1Nm : String? = null,

    @Column(name = "crew_1_telno", nullable = false, length = 12)
    var crew1Telno : String? = null,

    @Column(name = "crew_2_id", length = 15)
    var crew2Id : String? = null,

    @Column(name = "crew_2_pstn", length = 15)
    var crew2Pstn : String? = null,

    @Column(name = "crew_2_nm", length = 10)
    var crew2Nm : String? = null,

    @Column(name = "crew_2_telno", length = 12)
    var crew2Telno : String? = null,

    @Column(name = "crew_3_id", length = 15)
    var crew3Id : String? = null,

    @Column(name = "crew_3_pstn", length = 15)
    var crew3Pstn : String? = null,

    @Column(name = "crew_3_nm", length = 10)
    var crew3Nm : String? = null,

    @Column(name = "crew_3_telno", length = 12)
    var crew3Telno : String? = null,

    @Column(name = "chf_telno", nullable = false, length = 12)
    var chfTelno : String? = null,

    @Column(name = "vecno", nullable = false, length = 8)
    var vecno : String? = null,

    @Column(name = "msg", length = 500)
    var msg : String? = null,

    @Column(name = "dprt_dt", nullable = false, length = 8)
    var dprtDt: String? = null, // 출발 날짜

    @Column(name = "dprt_tm", nullable = false, length = 6)
    var dprtTm: String? = null, // 출발 시간

    @Column(name = "arvl_dt", nullable = false, length = 8)
    var arvlDt: String? = null, // 도착 날짜

    @Column(name = "arvl_tm", nullable = false, length = 6)
    var arvlTm: String? = null, // 도착 시간

    @Column(name = "trns_stat_cd", nullable = false, length = 8)
    var trnsStatCd: String? = null, // 이송 상태 코드
) : CommonEntity()

@Embeddable
data class BdasTrnsId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null, // 환자 ID

    @Column(name = "bdas_seq", nullable = false)
    var bdasSeq: Int? = null, // 병상 배정 순번
) : Serializable {

    companion object {
        private const val serialVersionUID = -3748060460236523127L
    }
}