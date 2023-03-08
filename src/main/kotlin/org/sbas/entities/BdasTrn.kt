package org.sbas.entities

import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "bdas_trns")
open class BdasTrn {
    @EmbeddedId
    open var id: BdasTrnId? = null

    @Column(name = "hist_cd", nullable = false, length = 8)
    open var histCd: String? = null

    @Column(name = "inst_id", nullable = false, length = 10)
    open var instId: String? = null

    @Column(name = "crew_id_1", nullable = false, length = 10)
    open var crewId1: String? = null

    @Column(name = "crew_id_2", length = 10)
    open var crewId2: String? = null

    @Column(name = "dprt_hosp_id", length = 10)
    open var dprtHospId: String? = null

    @Column(name = "dprt_dt", nullable = false, length = 8)
    open var dprtDt: String? = null

    @Column(name = "dprt_tm", nullable = false, length = 6)
    open var dprtTm: String? = null

    @Column(name = "arvl_hosp_id", nullable = false, length = 10)
    open var arvlHospId: String? = null

    @Column(name = "arvl_dt", nullable = false, length = 8)
    open var arvlDt: String? = null

    @Column(name = "arvl_tm", nullable = false, length = 6)
    open var arvlTm: String? = null

    @Column(name = "trns_stat_cd", nullable = false, length = 8)
    open var trnsStatCd: String? = null

    @Column(name = "rgst_user_id", nullable = false, length = 10)
    open var rgstUserId: String? = null

    @Column(name = "rgst_dttm", nullable = false)
    open var rgstDttm: Instant? = null

    @Column(name = "updt_user_id", nullable = false, length = 10)
    open var updtUserId: String? = null

    @Column(name = "updt_dttm", nullable = false)
    open var updtDttm: Instant? = null
}