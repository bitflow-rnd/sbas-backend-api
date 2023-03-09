package org.sbas.entities.info

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Serializable
@Entity
@Table(name = "info_inst")
class InfoInst(
    @Id
    @Column(name = "inst_id", nullable = false, length = 10)
    var id: String? = null,

    @Column(name = "inst_type_cd", nullable = false, length = 8)
    var instTypeCd: String? = null,

    @Column(name = "inst_nm", nullable = false, length = 100)
    var instNm: String? = null,

    @Column(name = "dstr_cd_1", nullable = false, length = 8)
    var dstrCd1: String? = null,

    @Column(name = "dstr_cd_2", nullable = false, length = 8)
    var dstrCd2: String? = null,

    @Column(name = "chrg_id", nullable = false, length = 10)
    var chrgId: String? = null,

    @Column(name = "chrg_nm", nullable = false, length = 10)
    var chrgNm: String? = null,

    @Column(name = "chrg_telno", nullable = false, length = 11)
    var chrgTelno: String? = null,

    @Column(name = "rmk", length = 200)
    var rmk: String? = null,

    @Column(name = "attc_id", length = 10)
    var attcId: String? = null,
) : CommonEntity(), java.io.Serializable {
    companion object {
        private const val serialVersionUID: Long = -3673425429591182434L
    }
}