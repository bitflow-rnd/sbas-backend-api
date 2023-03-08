package org.sbas.entities

import org.hibernate.Hibernate
import java.io.Serializable
import java.math.BigDecimal
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity

@Embeddable
open class BaseAttcId : Serializable {
    @Column(name = "attc_id", nullable = false, length = 10)
    open var attcId: String? = null

    @Column(name = "attc_seq", nullable = false, precision = 10)
    open var attcSeq: BigDecimal? = null

    override fun hashCode(): Int = Objects.hash(attcId, attcSeq)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as BaseAttcId

        return attcId == other.attcId &&
                attcSeq == other.attcSeq
    }

    companion object {
        private const val serialVersionUID = 1775098847550136173L
    }
}