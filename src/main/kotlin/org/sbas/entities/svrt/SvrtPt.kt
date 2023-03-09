package org.sbas.entities.svrt

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import java.io.Serial
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Serializable
@Entity
@Table(name = "svrt_pt")
class SvrtPt(
    @EmbeddedId
    var id: SvrtPtId? = null,

    @Column(name = "hosp_pid", nullable = false, length = 10)
    var hospPid: String? = null,

    @Column(name = "mon_strt_dt", nullable = false, length = 8)
    var monStrtDt: String? = null,

    @Column(name = "mon_end_dt", nullable = false, length = 8)
    var monEndDt: String? = null,

    @Column(name = "pt_type_cd", nullable = false, length = 10)
    var ptTypeCd: String? = null,

    @Column(name = "svrt_type_cd", length = 10)
    var svrtTypeCd: String? = null,

    @Column(name = "dept_nm", length = 20)
    var deptNm: String? = null,

    @Column(name = "ward_nm", length = 20)
    var wardNm: String? = null,

    @Column(name = "room_nm", length = 20)
    var roomNm: String? = null,

    @Column(name = "spcl_nm", nullable = false, length = 10)
    var spclNm: String? = null,

    @Column(name = "spcl_telno", length = 11)
    var spclTelno: String? = null,

) : CommonEntity(), java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = -8866642257118594358L
    }
}