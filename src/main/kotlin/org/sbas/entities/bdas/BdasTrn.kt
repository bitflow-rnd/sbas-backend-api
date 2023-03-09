package org.sbas.entities.bdas

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Serializable
@Entity
@Table(name = "bdas_trns")
class BdasTrn(
    @EmbeddedId
    var id: BdasTrnId? = null,

    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null,

    @Column(name = "inst_id", nullable = false, length = 10)
    var instId: String? = null,

    @Column(name = "crew_id_1", nullable = false, length = 10)
    var crewId1: String? = null,

    @Column(name = "crew_id_2", length = 10)
    var crewId2: String? = null,

    @Column(name = "dprt_hosp_id", length = 10)
    var dprtHospId: String? = null,

    @Column(name = "dprt_dt", nullable = false, length = 8)
    var dprtDt: String? = null,

    @Column(name = "dprt_tm", nullable = false, length = 6)
    var dprtTm: String? = null,

    @Column(name = "arvl_hosp_id", nullable = false, length = 10)
    var arvlHospId: String? = null,

    @Column(name = "arvl_dt", nullable = false, length = 8)
    var arvlDt: String? = null,

    @Column(name = "arvl_tm", nullable = false, length = 6)
    var arvlTm: String? = null,

    @Column(name = "trns_stat_cd", nullable = false, length = 8)
    var trnsStatCd: String? = null,
) : CommonEntity(), java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = 44633960065844049L
    }
}