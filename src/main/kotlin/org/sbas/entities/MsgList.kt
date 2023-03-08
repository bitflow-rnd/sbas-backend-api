package org.sbas.entities

import java.math.BigDecimal
import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "msg_list")
open class MsgList {
    @EmbeddedId
    open var id: MsgListId? = null

    @Column(name = "hist_cd", nullable = false, length = 8)
    open var histCd: String? = null

    @Column(name = "bdas_seq", precision = 10)
    open var bdasSeq: BigDecimal? = null

    @Column(name = "msg", length = 1000)
    open var msg: String? = null

    @Column(name = "attc_id", length = 10)
    open var attcId: String? = null

    @Column(name = "rgst_user_id", nullable = false, length = 10)
    open var rgstUserId: String? = null

    @Column(name = "rgst_dttm", nullable = false)
    open var rgstDttm: Instant? = null

    @Column(name = "updt_user_id", nullable = false, length = 10)
    open var updtUserId: String? = null

    @Column(name = "updt_dttm", nullable = false)
    open var updtDttm: Instant? = null
}