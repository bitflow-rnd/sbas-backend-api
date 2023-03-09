package org.sbas.entities.info

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Serializable
@Entity
@Table(name = "info_crew")
class InfoCrew(
    @EmbeddedId
    var id: InfoCrewId? = null,

    @Column(name = "crew_nm", nullable = false, length = 10)
    var crewNm: String? = null,

    @Column(name = "telno", nullable = false, length = 11)
    var telno: String? = null,

    @Column(name = "rmk", length = 200)
    var rmk: String? = null,
) : CommonEntity(), java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = -8096707614406150551L
    }
}