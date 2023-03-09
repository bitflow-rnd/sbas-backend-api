package org.sbas.entities.info

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import org.sbas.serializers.BigDecimalSerializer
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Serializable
@Entity
@Table(name = "info_user")
class InfoUser(
    @Id
    @Column(name = "user_id", nullable = false, length = 10)
    var id: String? = null,

    @Column(name = "pw", nullable = false, length = 100)
    var pw: String? = null,

    @Column(name = "user_nm", nullable = false, length = 10)
    var userNm: String? = null,

    @Column(name = "user_ci", nullable = false, length = 300)
    var userCi: String? = null,

    @Column(name = "push_key", nullable = false, length = 300)
    var pushKey: String? = null,

    @Column(name = "age", nullable = false, precision = 3)
    @Serializable(with = BigDecimalSerializer::class)
    var age: BigDecimal? = null,

    @Column(name = "gndr", nullable = false, length = 1)
    var gndr: String? = null,

    @Column(name = "telno", nullable = false, length = 11)
    var telno: String? = null,

    @Column(name = "job_cd", nullable = false, length = 8)
    var jobCd: String? = null,

    @Column(name = "ocp_cd", nullable = false, length = 8)
    var ocpCd: String? = null,

    @Column(name = "pt_type_cd", length = 8)
    var ptTypeCd: String? = null,

    @Column(name = "inst_type_cd", nullable = false, length = 10)
    var instTypeCd: String? = null,

    @Column(name = "inst_id", nullable = false, length = 10)
    var instId: String? = null,

    @Column(name = "inst_nm", nullable = false, length = 200)
    var instNm: String? = null,

    @Column(name = "duty_dstr_1_cd", nullable = false, length = 8)
    var dutyDstr1Cd: String? = null,

    @Column(name = "duty_dstr_2_cd", nullable = false, length = 8)
    var dutyDstr2Cd: String? = null,

    @Column(name = "duty_addr", length = 100)
    var dutyAddr: String? = null,

    @Column(name = "attc_id", length = 10)
    var attcId: String? = null,
) : CommonEntity(), java.io.Serializable {
    companion object {
        private const val serialVersionUID: Long = 2139983982022939410L
    }
}