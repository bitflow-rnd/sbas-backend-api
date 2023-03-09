package org.sbas.entities.bdas

import kotlinx.serialization.Serializable
import org.sbas.serializers.BigDecimalSerializer
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Embeddable

@Serializable
@Embeddable
class BdasTrnId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null,

    @Column(name = "bdas_seq", nullable = false, precision = 10)
    @Serializable(with = BigDecimalSerializer::class)
    var bdasSeq: BigDecimal? = null,

    @Column(name = "hist_seq", nullable = false, precision = 3)
    @Serializable(with = BigDecimalSerializer::class)
    var histSeq: BigDecimal? = null,
) : java.io.Serializable {

    companion object {
        private const val serialVersionUID = -3748060460236523127L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BdasTrnId) return false

        if (ptId != other.ptId) return false
        if (bdasSeq != other.bdasSeq) return false
        if (histSeq != other.histSeq) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ptId?.hashCode() ?: 0
        result = 31 * result + (bdasSeq?.hashCode() ?: 0)
        result = 31 * result + (histSeq?.hashCode() ?: 0)
        return result
    }
}