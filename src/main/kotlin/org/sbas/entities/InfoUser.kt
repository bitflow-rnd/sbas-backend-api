package org.sbas.entities

import java.math.BigDecimal
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "info_user")
open class InfoUser {
    @Id
    @Column(name = "user_id", nullable = false, length = 10)
    open var id: String? = null

    @Column(name = "pw", nullable = false, length = 100)
    open var pw: String? = null

    @Column(name = "user_nm", nullable = false, length = 10)
    open var userNm: String? = null

    @Column(name = "user_ci", nullable = false, length = 300)
    open var userCi: String? = null

    @Column(name = "push_key", nullable = false, length = 300)
    open var pushKey: String? = null

    @Column(name = "age", nullable = false, precision = 3)
    open var age: BigDecimal? = null

    @Column(name = "gndr", nullable = false, length = 1)
    open var gndr: String? = null

    @Column(name = "telno", nullable = false, length = 11)
    open var telno: String? = null

    @Column(name = "job_cd", nullable = false, length = 8)
    open var jobCd: String? = null

    @Column(name = "ocp_cd", nullable = false, length = 8)
    open var ocpCd: String? = null

    @Column(name = "pt_type_cd", length = 8)
    open var ptTypeCd: String? = null

    @Column(name = "inst_type_cd", nullable = false, length = 10)
    open var instTypeCd: String? = null

    @Column(name = "inst_id", nullable = false, length = 10)
    open var instId: String? = null

    @Column(name = "inst_nm", nullable = false, length = 200)
    open var instNm: String? = null

    @Column(name = "duty_dstr_1_cd", nullable = false, length = 8)
    open var dutyDstr1Cd: String? = null

    @Column(name = "duty_dstr_2_cd", nullable = false, length = 8)
    open var dutyDstr2Cd: String? = null

    @Column(name = "duty_addr", length = 100)
    open var dutyAddr: String? = null

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