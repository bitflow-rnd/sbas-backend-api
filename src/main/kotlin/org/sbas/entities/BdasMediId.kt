package org.sbas.entities

import org.hibernate.Hibernate
import java.io.Serializable
import java.math.BigDecimal
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity

@Embeddable
open class BdasMediId : Serializable {
    @Column(name = "pt_id", nullable = false, length = 10)
    open var ptId: String? = null

    @Column(name = "bdas_seq", nullable = false, precision = 10)
    open var bdasSeq: BigDecimal? = null

    @Column(name = "hist_seq", nullable = false, precision = 3)
    open var histSeq: BigDecimal? = null

    override fun hashCode(): Int = Objects.hash(ptId, bdasSeq, histSeq)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as BdasMediId

        return ptId == other.ptId &&
                bdasSeq == other.bdasSeq &&
                histSeq == other.histSeq
    }

    companion object {
        private const val serialVersionUID = -5971773794245461589L
    }
}