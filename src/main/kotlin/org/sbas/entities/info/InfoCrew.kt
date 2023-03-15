package org.sbas.entities.info

import org.sbas.entities.CommonEntity
import java.io.Serializable
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
) : CommonEntity()

@Embeddable
data class InfoCrewId(
    @Column(name = "inst_id", nullable = false, length = 10)
    var instId: String? = null, // 기관 ID

    @Column(name = "crew_id", nullable = false, length = 10)
    var crewId: String? = null, // 구급대원 ID
) : Serializable {

    companion object {
        private const val serialVersionUID = -4121112425160761203L
    }
}