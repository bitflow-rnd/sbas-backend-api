package org.sbas.entities.msg

import kotlinx.serialization.Serializable
import org.sbas.serializers.BigDecimalSerializer
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Embeddable

@Serializable
@Embeddable
class MsgListId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null,

    @Column(name = "msg_seq", nullable = false, precision = 10)
    @Serializable(with = BigDecimalSerializer::class)
    var msgSeq: BigDecimal? = null,

) : java.io.Serializable {

    companion object {
        private const val serialVersionUID = -474303298484540560L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MsgListId) return false

        if (ptId != other.ptId) return false
        if (msgSeq != other.msgSeq) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ptId?.hashCode() ?: 0
        result = 31 * result + (msgSeq?.hashCode() ?: 0)
        return result
    }
}