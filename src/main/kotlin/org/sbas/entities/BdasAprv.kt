package org.sbas.entities

import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "bdas_aprv")
open class BdasAprv {
    @EmbeddedId
    open var id: BdasAprvId? = null

    @Column(name = "hist_cd", nullable = false, length = 8)
    open var histCd: String? = null

    @Column(name = "hosp_id", nullable = false, length = 10)
    open var hospId: String? = null

    @Column(name = "room_nm", nullable = false, length = 20)
    open var roomNm: String? = null

    @Column(name = "aprv_dt", nullable = false, length = 10)
    open var aprvDt: String? = null

    @Column(name = "aprv_tm", nullable = false, length = 10)
    open var aprvTm: String? = null

    @Column(name = "aprv_yn", nullable = false, length = 8)
    open var aprvYn: String? = null

    @Column(name = "neg_cd", length = 6)
    open var negCd: String? = null

    @Column(name = "neg_detl", length = 1)
    open var negDetl: String? = null

    @Column(name = "rgst_user_id", nullable = false, length = 8)
    open var rgstUserId: String? = null

    @Column(name = "rgst_dttm", nullable = false, length = 500)
    open var rgstDttm: String? = null

    @Column(name = "updt_user_id", nullable = false, length = 10)
    open var updtUserId: String? = null

    @Column(name = "updt_dttm", nullable = false)
    open var updtDttm: Instant? = null
}