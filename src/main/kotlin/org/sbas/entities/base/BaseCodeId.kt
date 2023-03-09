package org.sbas.entities.base

import javax.persistence.Column
import javax.persistence.Embeddable
import kotlinx.serialization.Serializable


@Serializable
@Embeddable
class BaseCodeId(
    @Column(name = "cd_grp_id", nullable = false, length = 8)
    var cdGrpId: String? = null,

    @Column(name = "cd_id", nullable = false, length = 8)
    var cdId: String? = null,
) : java.io.Serializable {

    companion object {
        private const val serialVersionUID = -165979700881194617L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseCodeId

        if (cdGrpId != other.cdGrpId) return false
        if (cdId != other.cdId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cdGrpId?.hashCode() ?: 0
        result = 31 * result + (cdId?.hashCode() ?: 0)
        return result
    }
}