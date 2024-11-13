package org.sbas.entities.info

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.sbas.entities.CommonEntity
import java.io.Serializable

/**
 * 병원 정보
 */
@Entity
@Table(name = "info_hosp")
class InfoHosp(

    @Column(name = "hosp_id", nullable = false, length = 10)
    var hospId: String? = null, // 병원 ID

    @Id
    @Column(name = "hpid", nullable = false, length = 10)
    var hpId: String? = null, // 기관 ID

    @Column(name = "dutyname", nullable = false, length = 100)
    var dutyName: String? = null, // 기관명

    @Column(name = "dutydivnam", nullable = false, length = 50)
    var dutyDivNam: String? = null, // 병원분류명

    @Column(name = "dutytel1", nullable = false, length = 30)
    var dutyTel1: String? = null, // 대표전화1

    @Column(name = "dutytel3", nullable = false, length = 30)
    var dutyTel3: String? = null, // 응급실전화

    @Column(name = "dutyaddr", nullable = false, length = 200)
    var dutyAddr: String? = null, // 주소

    @Column(name = "wgs84lon", length = 19)
    var wgs84Lon: String? = null, // 병원경도

    @Column(name = "wgs84lat", length = 19)
    var wgs84Lat: String? = null, // 병원위도

    @Column(name = "dstr_1_cd", length = 8)
    var dstr1Cd: String? = null, // 시도

    @Column(name = "dstr_2_cd", length = 8)
    var dstr2Cd: String? = null, // 시군구

    @Column(name = "attc_id", length = 12)
    var attcId: String? = null, // 첨부 ID

) : CommonEntity(), Serializable {

    fun updateAttcId(attcId: String?) {
        this.attcId = attcId
    }

    companion object {
        private const val serialVersionUID: Long = -1587076961195755891L
    }
}