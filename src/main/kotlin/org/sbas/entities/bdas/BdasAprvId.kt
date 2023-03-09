package org.sbas.entities.bdas

import kotlinx.serialization.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable


@Serializable
@Embeddable
class BdasAprvId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null,

    @Column(name = "bdas_seq", nullable = false, precision = 10)
    var bdasSeq: Int? = null,

    @Column(name = "hist_seq", nullable = false, precision = 3)
    var histSeq: Int? = null,
) : java.io.Serializable {

    companion object {
        private const val serialVersionUID = -5290966609235076509L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BdasAprvId) return false

        if (ptId != other.ptId) return false
        if (bdasSeq != other.bdasSeq) return false
        if (histSeq != other.histSeq) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ptId?.hashCode() ?: 0
        result = 31 * result + (bdasSeq ?: 0)
        result = 31 * result + (histSeq ?: 0)
        return result
    }
}