package org.sbas.entities

import org.hibernate.Hibernate
import java.io.Serializable
import java.math.BigDecimal
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity

@Embeddable
open class MsgListId : Serializable {
    @Column(name = "pt_id", nullable = false, length = 10)
    open var ptId: String? = null

    @Column(name = "msg_seq", nullable = false, precision = 10)
    open var msgSeq: BigDecimal? = null

    override fun hashCode(): Int = Objects.hash(ptId, msgSeq)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as MsgListId

        return ptId == other.ptId &&
                msgSeq == other.msgSeq
    }

    companion object {
        private const val serialVersionUID = -474303298484540560L
    }
}