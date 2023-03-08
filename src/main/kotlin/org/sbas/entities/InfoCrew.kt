package org.sbas.entities

import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "info_crew")
open class InfoCrew {
    @EmbeddedId
    open var id: InfoCrewId? = null

    @Column(name = "crew_nm", nullable = false, length = 10)
    open var crewNm: String? = null

    @Column(name = "telno", nullable = false, length = 11)
    open var telno: String? = null

    @Column(name = "rmk", length = 200)
    open var rmk: String? = null

    @Column(name = "rgst_user_id", nullable = false, length = 10)
    open var rgstUserId: String? = null

    @Column(name = "rgst_dttm", nullable = false)
    open var rgstDttm: Instant? = null

    @Column(name = "updt_user_id", nullable = false, length = 10)
    open var updtUserId: String? = null

    @Column(name = "updt_dttm", nullable = false)
    open var updtDttm: Instant? = null
}