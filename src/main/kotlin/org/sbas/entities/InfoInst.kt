package org.sbas.entities

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "info_inst")
open class InfoInst {
    @Id
    @Column(name = "inst_id", nullable = false, length = 10)
    open var id: String? = null

    @Column(name = "inst_type_cd", nullable = false, length = 8)
    open var instTypeCd: String? = null

    @Column(name = "inst_nm", nullable = false, length = 100)
    open var instNm: String? = null

    @Column(name = "dstr_cd_1", nullable = false, length = 8)
    open var dstrCd1: String? = null

    @Column(name = "dstr_cd_2", nullable = false, length = 8)
    open var dstrCd2: String? = null

    @Column(name = "chrg_id", nullable = false, length = 10)
    open var chrgId: String? = null

    @Column(name = "chrg_nm", nullable = false, length = 10)
    open var chrgNm: String? = null

    @Column(name = "chrg_telno", nullable = false, length = 11)
    open var chrgTelno: String? = null

    @Column(name = "rmk", length = 200)
    open var rmk: String? = null

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