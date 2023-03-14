package org.sbas.entities.info

import org.sbas.entities.CommonEntity
import javax.persistence.*

/**
 * 구급대원 정보
 */
@Entity
@Table(name = "info_crew")
class InfoCrew(
    @EmbeddedId
    var id: InfoCrewId? = null,

    @Column(name = "crew_nm", nullable = false, length = 10)
    var crewNm: String? = null, // 구급대원 이름

    @Column(name = "telno", nullable = false, length = 11)
    var telno: String? = null, // 전화번호

    @Column(name = "rmk", length = 200)
    var rmk: String? = null, // 비고
) : CommonEntity(), java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = -8096707614406150551L
    }
}

@Embeddable
data class InfoCrewId(
    @Column(name = "inst_id", nullable = false, length = 10)
    var instId: String? = null, // 기관 ID

    @Column(name = "crew_id", nullable = false, length = 10)
    var crewId: String? = null, // 구급대원 ID
) : java.io.Serializable {

    companion object {
        private const val serialVersionUID = -4121112425160761203L
    }
}