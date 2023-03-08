package org.sbas.entities

import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "svrt_pt")
open class SvrtPt {
    @EmbeddedId
    open var id: SvrtPtId? = null

    @Column(name = "hosp_pid", nullable = false, length = 10)
    open var hospPid: String? = null

    @Column(name = "mon_strt_dt", nullable = false, length = 8)
    open var monStrtDt: String? = null

    @Column(name = "mon_end_dt", nullable = false, length = 8)
    open var monEndDt: String? = null

    @Column(name = "pt_type_cd", nullable = false, length = 10)
    open var ptTypeCd: String? = null

    @Column(name = "svrt_type_cd", length = 10)
    open var svrtTypeCd: String? = null

    @Column(name = "dept_nm", length = 20)
    open var deptNm: String? = null

    @Column(name = "ward_nm", length = 20)
    open var wardNm: String? = null

    @Column(name = "room_nm", length = 20)
    open var roomNm: String? = null

    @Column(name = "spcl_nm", nullable = false, length = 10)
    open var spclNm: String? = null

    @Column(name = "spcl_telno", length = 11)
    open var spclTelno: String? = null

    @Column(name = "rgst_user_id", nullable = false, length = 10)
    open var rgstUserId: String? = null

    @Column(name = "rgst_dttm", nullable = false)
    open var rgstDttm: Instant? = null

    @Column(name = "updt_user_id", nullable = false, length = 10)
    open var updtUserId: String? = null

    @Column(name = "updt_dttm", nullable = false)
    open var updtDttm: Instant? = null
}