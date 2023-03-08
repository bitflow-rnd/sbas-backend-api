package org.sbas.entities

import org.hibernate.Hibernate
import java.io.Serializable
import java.math.BigDecimal
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity

@Embeddable
open class BdasSympId : Serializable {
    @Column(name = "pt_id", nullable = false, length = 10)
    open var ptId: String? = null

    @Column(name = "bdas_seq", nullable = false, precision = 10)
    open var bdasSeq: BigDecimal? = null

    override fun hashCode(): Int = Objects.hash(ptId, bdasSeq)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as BdasSympId

        return ptId == other.ptId &&
                bdasSeq == other.bdasSeq
    }

    companion object {
        private const val serialVersionUID = 5725666136275167985L
    }
}