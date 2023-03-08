package org.sbas.entities

import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "base_attc")
open class BaseAttc {
    @EmbeddedId
    open var id: BaseAttcId? = null

    @Column(name = "attc_dt", nullable = false, length = 8)
    open var attcDt: String? = null

    @Column(name = "attc_tm", nullable = false, length = 6)
    open var attcTm: String? = null

    @Column(name = "file_type_cd", nullable = false, length = 8)
    open var fileTypeCd: String? = null

    @Column(name = "path_str", nullable = false, length = 100)
    open var pathStr: String? = null

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