package org.sbas.entities.bdas

import org.sbas.entities.CommonEntity
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "bdas_req_aprv")
class BdasReqAprv(
    @EmbeddedId
    var id: BdasReqAprvId,

    @Column(name = "aprv_yn", nullable = false, length = 1)
    var aprvYn: String, // 승인 여부

    @Column(name = "neg_cd", length = 8)
    var negCd: String? = null, // 불가 코드

    @Column(name = "msg", length = 500)
    var msg: String? = null, // 메시지

    @Column(name = "req_hosp_id", length = 10)
    var reqHospId: String? = null,

    @Column(name = "req_hosp_nm", length = 50)
    var reqHospNm: String? = null,
) : CommonEntity() {

    fun convertToBdasAprv(): BdasAprv {
        return BdasAprv(
            id = BdasAprvId(
                ptId = this.id.ptId,
                bdasSeq = this.id.bdasSeq,
                asgnReqSeq = this.id.asgnReqSeq,
            ),
            hospId = this.reqHospId!!,
            aprvYn = "N",
            negCd = "BNRN0008",
            msg = "이미 배정 승인된 병원이 존재하여 불가 처리되었습니다.",
        )
    }
}

@Embeddable
data class BdasReqAprvId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String, // 환자 ID

    @Column(name = "bdas_seq", nullable = false)
    var bdasSeq: Int, // 병상 배정 순번

    @Column(name = "asgn_req_seq", nullable = false)
    var asgnReqSeq: Int,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 4799827589879104454L
    }
}