package org.sbas.entities

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "info_hosp")
open class InfoHosp {
    @Id
    @Column(name = "hosp_id", nullable = false, length = 10)
    open var id: String? = null

    @Column(name = "hosp_id_egen", length = 10)
    open var hospIdEgen: String? = null

    @Column(name = "attr_id", nullable = false, length = 10)
    open var attrId: String? = null

    @Column(name = "attr_val", length = 200)
    open var attrVal: String? = null

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