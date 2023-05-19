package org.sbas.entities.bdas

import org.sbas.constants.AsgnStat
import org.sbas.entities.CommonEntity
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "bdas_asgn_aprv")
class BdasAsgnAprv(
    @EmbeddedId
    var id: BdasAsgnAprvId? = null,

    @Column(name = "aprv_yn", nullable = false, length = 1)
    var aprvYn: String? = null, // 승인 여부

    @Column(name = "neg_cd", length = 8)
    var negCd: String? = null, // 불가 코드

    @Column(name = "msg", length = 500)
    var msg: String? = null, // 메시지

    @Column(name = "req_hosp_id", length = 10)
    var reqHospId: String? = null,

    @Column(name = "req_hosp_nm", length = 50)
    var reqHospNm: String? = null,

    @Column(name = "asgn_stat", length = 8)
    var asgnStat: String? = null,
) : CommonEntity()

@Embeddable
data class BdasAsgnAprvId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null, // 환자 ID

    @Column(name = "bdas_seq", nullable = false)
    var bdasSeq: Int? = null, // 병상 배정 순번

    @Column(name = "asgn_req_seq", nullable = false)
    var asgnReqSeq: Int? = null,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 4799827589879104454L
    }
}