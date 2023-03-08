package org.sbas.entities

import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "bdas_symp")
open class BdasSymp {
    @EmbeddedId
    open var id: BdasSympId? = null

    @Column(name = "hist_cd", length = 8)
    open var histCd: String? = null

    @Column(name = "hist_seq", precision = 3)
    open var histSeq: BigDecimal? = null

    @Column(name = "attr_id", precision = 10)
    open var attrId: BigDecimal? = null

    @Column(name = "attr_val")
    open var attrVal: ByteArray? = null

    @Column(name = "rmk", length = 50)
    open var rmk: String? = null

    @Column(name = "rgst_user_id", nullable = false, length = 11)
    open var rgstUserId: String? = null

    @Column(name = "rgst_dttm", nullable = false, length = 100)
    open var rgstDttm: String? = null

    @Column(name = "updt_user_id", nullable = false, length = 11)
    open var updtUserId: String? = null

    @Column(name = "updt_dttm", nullable = false, length = 10)
    open var updtDttm: String? = null
}