package org.sbas.entities.svrt

import kotlinx.serialization.Serializable
import org.hibernate.Hibernate
import org.sbas.serializers.BigDecimalSerializer
import java.math.BigDecimal
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable

@Serializable
@Embeddable
class SvrtAnlyId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null,

    @Column(name = "rgst_seq", nullable = false, precision = 10)
    @Serializable(with = BigDecimalSerializer::class)
    var rgstSeq: BigDecimal? = null,

) : java.io.Serializable {

    companion object {
        private const val serialVersionUID = 4678885074474623613L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SvrtAnlyId) return false

        if (ptId != other.ptId) return false
        if (rgstSeq != other.rgstSeq) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ptId?.hashCode() ?: 0
        result = 31 * result + (rgstSeq?.hashCode() ?: 0)
        return result
    }
}