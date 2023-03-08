package org.sbas.entities

import java.math.BigDecimal
import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "base_attr")
open class BaseAttr {
    @EmbeddedId
    open var id: BaseAttrId? = null

    @Column(name = "attr_lvl", nullable = false, precision = 1)
    open var attrLvl: BigDecimal? = null

    @Column(name = "pr_attr_id", nullable = false, length = 20)
    open var prAttrId: String? = null

    @Column(name = "attr_nm", nullable = false, length = 100)
    open var attrNm: String? = null

    @Column(name = "attr_seq", nullable = false, precision = 1)
    open var attrSeq: BigDecimal? = null

    @Column(name = "attr_val", length = 200)
    open var attrVal: String? = null

    @Column(name = "val_type_cd", length = 8)
    open var valTypeCd: String? = null

    @Column(name = "app_end_dt", nullable = false, length = 8)
    open var appEndDt: String? = null

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