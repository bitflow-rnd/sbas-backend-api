package org.sbas.entities.bdas

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import java.io.Serial
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Serializable
@Entity
@Table(name = "bdas_medi")
class BdasMedi(
    @EmbeddedId
    var id: BdasMediId? = null,

    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null,

    @Column(name = "hosp_id", nullable = false, length = 10)
    var hospId: String? = null,

    @Column(name = "dept_nm", nullable = false, length = 20)
    var deptNm: String? = null,

    @Column(name = "stff_id", nullable = false, length = 10)
    var stffId: String? = null,

    @Column(name = "stff_nm", nullable = false, length = 10)
    var stffNm: String? = null,

    @Column(name = "cnfm_dt", nullable = false, length = 8)
    var cnfmDt: String? = null,

    @Column(name = "cnfm_tm", nullable = false, length = 6)
    var cnfmTm: String? = null,

    @Column(name = "aprv_yn", nullable = false, length = 1)
    var aprvYn: String? = null,

    @Column(name = "neg_cd", length = 8)
    var negCd: String? = null,

    @Column(name = "neg_detl", length = 500)
    var negDetl: String? = null,
) : CommonEntity(), java.io.Serializable{

    companion object {
        private const val serialVersionUID: Long = -5775047080640955569L
    }
}