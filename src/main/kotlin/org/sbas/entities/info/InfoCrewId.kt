package org.sbas.entities.info

import kotlinx.serialization.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Serializable
@Embeddable
class InfoCrewId(
    @Column(name = "inst_id", nullable = false, length = 10)
    var instId: String? = null,

    @Column(name = "crew_id", nullable = false, length = 10)
var crewId: String? = null,
) : java.io.Serializable {

    companion object {
        private const val serialVersionUID = -4121112425160761203L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InfoCrewId) return false

        if (instId != other.instId) return false
        if (crewId != other.crewId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = instId?.hashCode() ?: 0
        result = 31 * result + (crewId?.hashCode() ?: 0)
        return result
    }
}