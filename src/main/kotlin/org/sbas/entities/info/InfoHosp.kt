package org.sbas.entities.info

import org.sbas.entities.CommonEntity
import java.io.Serializable
import jakarta.persistence.*

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

    @Column(name = "dutytel1", nullable = false, length = 19)
    var dutyTel1: String? = null, // 대표전화1

    @Column(name = "dutyaddr", nullable = false, length = 200)
    var dutyAddr: String? = null, // 주소

    @Column(name = "postcdn1", length = 3)
    var postCdn1: String? = null, // 우편번호1

    @Column(name = "postcdn2", length = 3)
    var postCdn2: String? = null, // 우편번호2

    @Column(name = "wgs84lon", length = 19)
    var wgs84Lon: String? = null, // 병원경도

    @Column(name = "wgs84lat", length = 19)
    var wgs84Lat: String? = null, // 병원위도

    @Column(name = "dutyinf", length = 300)
    var dutyInf: String? = null, // 기관설명상세

    @Column(name = "dutyetc")
    var dutyEtc: String? = null, // 비고

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

//@Embeddable
//data class InfoHospId(
//    @GenericGenerator(
//        name = "info_hosp_id_seq",
//        strategy = "org.sbas.entities.StringPrefixedSequenceIdGenerator",
//        parameters = [
//            Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "HP"),
//            Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%08d"),
//            Parameter(name = StringPrefixedSequenceIdGenerator.incrementSize, value = "1")
//        ]) // HP00000001
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "info_hosp_id_seq")
//    @Column(name = "hosp_id", nullable = false, length = 10)
//    var hospId: String? = null, // 병원 ID
//
//    @Column(name = "hpid", nullable = false, length = 10)
//    var hpId: String? = null, // 기관 ID
//) : Serializable {
//
//    companion object {
//        private const val serialVersionUID: Long = -1587076961195755891L
//    }
//}