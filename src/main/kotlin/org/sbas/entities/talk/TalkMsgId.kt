package org.sbas.entities.talk

import org.hibernate.Hibernate
import java.io.Serializable
import java.math.BigDecimal
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Embeddable
class TalkMsgId : Serializable {
    @Size(max = 10)
    @NotNull
    @Column(name = "tkrm_id", nullable = false, length = 10)
    var tkrmId: String? = null

    @NotNull
    @Column(name = "msg_seq", nullable = false, precision = 3)
    var msgSeq: BigDecimal? = null

    @NotNull
    @Column(name = "hist_seq", nullable = false, precision = 3)
    var histSeq: BigDecimal? = null

    override fun hashCode(): Int = Objects.hash(tkrmId, msgSeq, histSeq)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as TalkMsgId

        return tkrmId == other.tkrmId &&
                msgSeq == other.msgSeq &&
                histSeq == other.histSeq
    }

    companion object {
        private const val serialVersionUID = -544776586376217282L
    }
}