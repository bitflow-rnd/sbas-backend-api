package org.sbas.entities

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "info_pt")
open class InfoPt {
    @Id
    @Column(name = "pt_id", nullable = false, length = 10)
    open var id: String? = null

    @Column(name = "pt_nm", nullable = false, length = 10)
    open var ptNm: String? = null

    @Column(name = "gndr", nullable = false, length = 1)
    open var gndr: String? = null

    @Column(name = "rrno_1", nullable = false, length = 6)
    open var rrno1: String? = null

    @Column(name = "rrno_2", nullable = false, length = 7)
    open var rrno2: String? = null

    @Column(name = "dstr_1_cd", nullable = false, length = 8)
    open var dstr1Cd: String? = null

    @Column(name = "dstr_2_cd", nullable = false, length = 8)
    open var dstr2Cd: String? = null

    @Column(name = "addr", nullable = false, length = 100)
    open var addr: String? = null

    @Column(name = "telno", nullable = false, length = 11)
    open var telno: String? = null

    @Column(name = "nati_cd", nullable = false, length = 8)
    open var natiCd: String? = null

    @Column(name = "pica_ver", length = 10)
    open var picaVer: String? = null

    @Column(name = "rgst_user_id", nullable = false, length = 10)
    open var rgstUserId: String? = null

    @Column(name = "rgst_dttm", nullable = false)
    open var rgstDttm: Instant? = null

    @Column(name = "updt_user_id", nullable = false, length = 10)
    open var updtUserId: String? = null

    @Column(name = "updt_dttm", nullable = false)
    open var updtDttm: Instant? = null
}