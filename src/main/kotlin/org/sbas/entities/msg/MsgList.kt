package org.sbas.entities.msg

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import org.sbas.serializers.BigDecimalSerializer
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Serializable
@Entity
@Table(name = "msg_list")
class MsgList(
    @EmbeddedId
    var id: MsgListId? = null,

    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null,

    @Column(name = "bdas_seq", precision = 10)
    @Serializable(with = BigDecimalSerializer::class)
    var bdasSeq: BigDecimal? = null,

    @Column(name = "msg", length = 1000)
    var msg: String? = null,

    @Column(name = "attc_id", length = 10)
    var attcId: String? = null,

    ) : CommonEntity(), java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = -804783972843389739L
    }
}