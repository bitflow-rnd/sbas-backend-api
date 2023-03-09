package org.sbas.entities.bdas

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Serializable
@Entity
@Table(name = "bdas_adms")
class BdasAdm(
    @EmbeddedId
    var id: BdasAdmId? = null,

    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null,

    @Column(name = "hosp_id", nullable = false, length = 10)
    var hospId: String? = null,

    @Column(name = "dept_nm", length = 20)
    var deptNm: String? = null,

    @Column(name = "ward_nm", length = 20)
    var wardNm: String? = null,

    @Column(name = "room_nm", length = 20)
    var roomNm: String? = null,

    @Column(name = "adms_dt", nullable = false, length = 8)
    var admsDt: String? = null,

    @Column(name = "adms_tm", nullable = false, length = 6)
    var admsTm: String? = null,

    @Column(name = "dsch_dt", length = 8)
    var dschDt: String? = null,

    @Column(name = "dsch_tm", length = 6)
    var dschTm: String? = null,

    @Column(name = "dsch_rsn_cd", length = 8)
    var dschRsnCd: String? = null,

    @Column(name = "dsch_rsn_detl", length = 500)
    var dschRsnDetl: String? = null,
) : CommonEntity(), java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = 2241635970857630703L
    }
}


