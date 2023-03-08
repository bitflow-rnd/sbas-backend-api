package org.sbas.entities

import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "bdas_adms")
open class BdasAdm {
    @EmbeddedId
    open var id: BdasAdmId? = null

    @Column(name = "hist_cd", nullable = false, length = 8)
    open var histCd: String? = null

    @Column(name = "hosp_id", nullable = false, length = 10)
    open var hospId: String? = null

    @Column(name = "dept_nm", length = 20)
    open var deptNm: String? = null

    @Column(name = "ward_nm", length = 20)
    open var wardNm: String? = null

    @Column(name = "room_nm", length = 20)
    open var roomNm: String? = null

    @Column(name = "adms_dt", nullable = false, length = 8)
    open var admsDt: String? = null

    @Column(name = "adms_tm", nullable = false, length = 6)
    open var admsTm: String? = null

    @Column(name = "dsch_dt", length = 8)
    open var dschDt: String? = null

    @Column(name = "dsch_tm", length = 6)
    open var dschTm: String? = null

    @Column(name = "dsch_rsn_cd", length = 8)
    open var dschRsnCd: String? = null

    @Column(name = "dsch_rsn_detl", length = 500)
    open var dschRsnDetl: String? = null

    @Column(name = "rgst_user_id", nullable = false, length = 10)
    open var rgstUserId: String? = null

    @Column(name = "rgst_dttm", nullable = false)
    open var rgstDttm: Instant? = null

    @Column(name = "updt_user_id", nullable = false, length = 10)
    open var updtUserId: String? = null

    @Column(name = "updt_dttm", nullable = false)
    open var updtDttm: Instant? = null
}