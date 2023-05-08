package org.sbas.entities.info

import org.sbas.entities.CommonEntity
import java.io.Serializable
import javax.persistence.*

/**
 * 병원 정보
 */
@Entity
@Table(name = "info_hosp")
class InfoHosp(
    @EmbeddedId
    var id: InfoHospId? = null,

    @Column(name = "dutyname", nullable = false, length = 100)
    var dutyname: String? = null, // 기관명

    @Column(name = "dutydivnam", nullable = false, length = 50)
    var dutydivnam: String? = null, // 병원분류명

    @Column(name = "dutytel1", nullable = false, length = 14)
    var dutytel1: String? = null, // 대표전화1

    @Column(name = "dutyaddr", nullable = false, length = 200)
    var dutyaddr: String? = null, // 주소

    @Column(name = "postcdn1", length = 3)
    var postcdn1: String? = null, // 우편번호1

    @Column(name = "postcdn2", length = 3)
    var postcdn2: String? = null, // 우편번호2

    @Column(name = "wgs84lon", length = 19)
    var wgs84lon: String? = null, // 병원경도

    @Column(name = "wgs84lat", length = 19)
    var wgs84lat: String? = null, // 병원위도

    @Column(name = "dutyinf", length = 300)
    var dutyinf: String? = null, // 기관설명상세
    
    @Column(name = "dutyetc", length = 200)
    var dutyetc: String? = null, // 비고
) : CommonEntity()

@Embeddable
data class InfoHospId(
    @Column(name = "hosp_id", nullable = false, length = 10)
    var hospId: String? = null, // 병원 ID

    @Column(name = "hpid", nullable = false, length = 10)
    var hpId: String? = null, // 기관 ID
) : Serializable {

    companion object {
        private const val serialVersionUID: Long = -1587076961195755891L
    }
}