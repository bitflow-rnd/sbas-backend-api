package org.sbas.entities.info

import java.io.Serializable
import java.time.Instant
import javax.persistence.*

/**
 * 초대 정보
 */
@Entity
@Table(name = "info_invt")
class InfoInvt(
    @EmbeddedId
    var id: InfoInvtId,

    @Column(name = "invt_nm", nullable = false, length = 10)
    var invtNm: String, // 초대 대상 이름

    @Column(name = "invt_dt", nullable = false, length = 8)
    var invtDt: String, // 초대 날짜

    @Column(name = "invt_tm", nullable = false, length = 6)
    var invtTm: String, // 초대 시간

    @Column(name = "cnfm_dt", length = 8)
    var cnfmDt: String, // 확인 날짜

    @Column(name = "cnfm_tm", length = 6)
    var cnfmTm: String, // 확인 시간

    @Column(name = "rgst_req_user_id", length = 15)
    var rgstReqUserId: String, // 등록 요청 사용자 ID

    @Column(name = "rgst_req_dttm")
    var rgstReqDttm: Instant, // 등록 요청 일시

    @Column(name = "rgst_aprv_user_id", length = 15)
    var rgstAprvUserId: String, // 등록 승인 사용자 ID

    @Column(name = "rgst_aprv_dttm")
    var rgstAprvDttm: Instant, // 등록 승인 일시
)

@Embeddable
data class InfoInvtId(
    @Column(name = "user_id", nullable = false, length = 15)
    var userId: String, // 사용자 ID

    @Column(name = "invt_telno", nullable = false, length = 11)
    var invtTelno: String, // 초대 대상 전화번호
) : Serializable {

    companion object {
        private const val serialVersionUID: Long = 1654111187240040081L
    }
}