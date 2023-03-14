package org.sbas.entities.msg

import org.sbas.entities.CommonEntity
import java.math.BigDecimal
import javax.persistence.*

/**
 * 환자별 메시지
 */
@Entity
@Table(name = "msg_list")
class MsgList(
    @EmbeddedId
    var id: MsgListId? = null,

    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null, // 이력 코드

    @Column(name = "bdas_seq", precision = 10)
    var bdasSeq: BigDecimal? = null, // 병상 배정 순번

    @Column(name = "msg", length = 1000)
    var msg: String? = null, // 메시지

    @Column(name = "attc_id", length = 10)
    var attcId: String? = null, // 첨부 ID

    ) : CommonEntity(), java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = -804783972843389739L
    }
}

@Embeddable
class MsgListId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null, // 환자 ID

    @Column(name = "msg_seq", nullable = false, precision = 10)
    var msgSeq: BigDecimal? = null, // 메시지 순번

    @Column(name = "hist_seq", nullable = false, precision = 3)
    var histSeq: Int? = null, // 이력 순번
) : java.io.Serializable {

    companion object {
        private const val serialVersionUID = -474303298484540560L
    }
}