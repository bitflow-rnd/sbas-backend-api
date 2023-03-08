package org.sbas.entities

import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity

@Embeddable
open class InfoCrewId : Serializable {
    @Column(name = "inst_id", nullable = false, length = 10)
    open var instId: String? = null

    @Column(name = "crew_id", nullable = false, length = 10)
    open var crewId: String? = null

    override fun hashCode(): Int = Objects.hash(instId, crewId)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as InfoCrewId

        return instId == other.instId &&
                crewId == other.crewId
    }

    companion object {
        private const val serialVersionUID = -4121112425160761203L
    }
}