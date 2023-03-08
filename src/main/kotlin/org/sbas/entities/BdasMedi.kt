package org.sbas.entities

import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "bdas_medi")
open class BdasMedi {
    @EmbeddedId
    open var id: BdasMediId? = null

    @Column(name = "hist_cd", nullable = false, length = 8)
    open var histCd: String? = null

    @Column(name = "hosp_id", nullable = false, length = 10)
    open var hospId: String? = null

    @Column(name = "dept_nm", nullable = false, length = 20)
    open var deptNm: String? = null

    @Column(name = "stff_id", nullable = false, length = 10)
    open var stffId: String? = null

    @Column(name = "stff_nm", nullable = false, length = 10)
    open var stffNm: String? = null

    @Column(name = "cnfm_dt", nullable = false, length = 8)
    open var cnfmDt: String? = null

    @Column(name = "cnfm_tm", nullable = false, length = 6)
    open var cnfmTm: String? = null

    @Column(name = "aprv_yn", nullable = false, length = 1)
    open var aprvYn: String? = null

    @Column(name = "neg_cd", length = 8)
    open var negCd: String? = null

    @Column(name = "neg_detl", length = 500)
    open var negDetl: String? = null

    @Column(name = "rgst_user_id", nullable = false, length = 10)
    open var rgstUserId: String? = null

    @Column(name = "rgst_dttm", nullable = false)
    open var rgstDttm: Instant? = null

    @Column(name = "updt_user_id", nullable = false, length = 10)
    open var updtUserId: String? = null

    @Column(name = "updt_dttm", nullable = false)
    open var updtDttm: Instant? = null
}