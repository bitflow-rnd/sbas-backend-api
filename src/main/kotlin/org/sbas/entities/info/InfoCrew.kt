package org.sbas.entities.info

import jakarta.persistence.*
import org.sbas.dtos.info.InfoCrewDto
import org.sbas.dtos.info.InfoCrewUpdateReq
import org.sbas.entities.CommonEntity
import java.io.Serializable

/**
 * 구급대원 정보
 */
@Entity
@Table(name = "info_crew")
class InfoCrew(
    @EmbeddedId
    var id: InfoCrewId,

    @Column(name = "crew_nm", nullable = false, length = 10)
    var crewNm: String, // 구급대원 이름

    @Column(name = "telno", length = 11)
    var telno: String? = null, // 전화번호

    @Column(name = "rmk", length = 200)
    var rmk: String? = null, // 비고

    @Column(name = "pstn", length = 15)
    var pstn: String? = null,

) : CommonEntity() {

    fun toInfoCrewDto(): InfoCrewDto {
        return InfoCrewDto(
            instId = id.instId,
            crewId = id.crewId,
            crewNm = crewNm,
            telno = telno,
            rmk = rmk,
            pstn = pstn,
        )
    }

    fun update(req: InfoCrewUpdateReq) {
        with(req) {
            crewNm.let { this@InfoCrew.crewNm = it }
            telno?.let { this@InfoCrew.telno = it }
            rmk?.let { this@InfoCrew.rmk = it }
            pstn?.let { this@InfoCrew.pstn = it }
        }
    }

  fun update(req: InfoCrew) {
    with(req) {
      crewNm.let { this@InfoCrew.crewNm = it }
      telno?.let { this@InfoCrew.telno = it }
      rmk?.let { this@InfoCrew.rmk = it }
      pstn?.let { this@InfoCrew.pstn = it }
    }
  }
}

@Embeddable
data class InfoCrewId(
    @Column(name = "inst_id", nullable = false, length = 10)
    var instId: String, // 기관 ID

    @Column(name = "crew_id", nullable = false)
    var crewId: Int, // 구급대원 ID
) : Serializable {

    companion object {
        private const val serialVersionUID = -4121112425160761203L
    }
}