package org.sbas.entities.bdas

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import java.io.Serial
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "bdas_aprv")
class BdasAprv(
    @EmbeddedId
    var id: BdasAprvId? = null,

    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null,

    @Column(name = "hosp_id", nullable = false, length = 10)
    var hospId: String? = null,

    @Column(name = "room_nm", nullable = false, length = 20)
    var roomNm: String? = null,

    @Column(name = "aprv_dt", nullable = false, length = 10)
    var aprvDt: String? = null,

    @Column(name = "aprv_tm", nullable = false, length = 10)
    var aprvTm: String? = null,

    @Column(name = "aprv_yn", nullable = false, length = 8)
    var aprvYn: String? = null,

    @Column(name = "neg_cd", length = 6)
    var negCd: String? = null,

    @Column(name = "neg_detl", length = 1)
    var negDetl: String? = null,
) : CommonEntity(), java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = -2233279039719269210L
    }
}