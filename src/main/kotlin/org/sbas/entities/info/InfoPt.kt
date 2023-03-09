package org.sbas.entities.info

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Serializable
@Entity
@Table(name = "info_pt")
class InfoPt(
    @Id
    @Column(name = "pt_id", nullable = false, length = 10)
    var id: String? = null,

    @Column(name = "pt_nm", nullable = false, length = 10)
    var ptNm: String? = null,

    @Column(name = "gndr", nullable = false, length = 1)
    var gndr: String? = null,

    @Column(name = "rrno_1", nullable = false, length = 6)
    var rrno1: String? = null,

    @Column(name = "rrno_2", nullable = false, length = 7)
    var rrno2: String? = null,

    @Column(name = "dstr_1_cd", nullable = false, length = 8)
    var dstr1Cd: String? = null,

    @Column(name = "dstr_2_cd", nullable = false, length = 8)
    var dstr2Cd: String? = null,

    @Column(name = "addr", nullable = false, length = 100)
    var addr: String? = null,

    @Column(name = "telno", nullable = false, length = 11)
    var telno: String? = null,

    @Column(name = "nati_cd", nullable = false, length = 8)
    var natiCd: String? = null,

    @Column(name = "pica_ver", length = 10)
    var picaVer: String? = null,
) : CommonEntity(), java.io.Serializable {
    companion object {
        private const val serialVersionUID: Long = 8_117_578_039_832_831_706L
    }
}