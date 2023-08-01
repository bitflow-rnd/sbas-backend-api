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
    var id: BdasAprvId,

    @Column(name = "hosp_id", length = 10)
    var hospId: String, // 병원 ID

    @Column(name = "room_nm", length = 20)
    var roomNm: String? = null, // 병실 이름

    @Column(name = "dept_nm", length = 20)
    var deptNm: String? = null, // 진료과 이름

    @Column(name = "spcl_id", length = 15)
    var spclId: String? = null, // 담당의 ID

    @Column(name = "spcl_nm", length = 10)
    var spclNm: String? = null, // 담당의 이름

    @Column(name = "chrg_telno", length = 12)
    var chrgTelno: String? = null, // 담당 전화번호
    
    @Column(name = "aprv_dt", length = 8)
    var aprvDt: String? = null, // 승인 날짜

    @Column(name = "aprv_tm", length = 6)
    var aprvTm: String? = null, // 승인 시간

    @Column(name = "aprv_yn", nullable = false, length = 1)
    var aprvYn: String, // 승인 여부

    @Column(name = "neg_cd", length = 8)
    var negCd: String? = null, // 불가 코드

    @Column(name = "msg", length = 500)
    var msg: String? = null, // 메시지
) : CommonEntity()

@Embeddable
data class BdasAprvId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String, // 환자 ID

    @Column(name = "bdas_seq", nullable = false)
    var bdasSeq: Int, // 병상 배정 순번

    @Column(name = "asgn_req_seq", nullable = false)
    var asgnReqSeq: Int,
) : Serializable {

    companion object {
        private const val serialVersionUID = -5290966609235076509L
    }
}