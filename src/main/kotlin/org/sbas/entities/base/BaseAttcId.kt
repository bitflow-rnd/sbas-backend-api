package org.sbas.entities.base

import kotlinx.serialization.Serializable
import org.sbas.serializers.BigDecimalSerializer
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Embeddable

@Serializable
@Embeddable
class BaseAttcId(
    @Column(name = "attc_id", nullable = false, length = 10)
    var attcId: String? = null,

    @Column(name = "attc_seq", nullable = false, precision = 10)
    @Serializable(with = BigDecimalSerializer::class)
    var attcSeq: BigDecimal? = null,

) : java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = -2680113473810765109L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseAttcId) return false

        if (attcId != other.attcId) return false
        if (attcSeq != other.attcSeq) return false

        return true
    }

    override fun hashCode(): Int {
        var result = attcId?.hashCode() ?: 0
        result = 31 * result + (attcSeq?.hashCode() ?: 0)
        return result
    }
}