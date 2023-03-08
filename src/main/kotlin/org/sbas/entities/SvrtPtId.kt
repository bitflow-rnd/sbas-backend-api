package org.sbas.entities

import org.hibernate.Hibernate
import java.io.Serializable
import java.math.BigDecimal
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity

@Embeddable
open class SvrtPtId : Serializable {
    @Column(name = "pt_id", nullable = false, length = 10)
    open var ptId: String? = null

    @Column(name = "hosp_id", nullable = false, length = 10)
    open var hospId: String? = null

    @Column(name = "rgst_seq", nullable = false, precision = 10)
    open var rgstSeq: BigDecimal? = null

    override fun hashCode(): Int = Objects.hash(ptId, hospId, rgstSeq)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as SvrtPtId

        return ptId == other.ptId &&
                hospId == other.hospId &&
                rgstSeq == other.rgstSeq
    }

    companion object {
        private const val serialVersionUID = -8539487836216536155L
    }
}