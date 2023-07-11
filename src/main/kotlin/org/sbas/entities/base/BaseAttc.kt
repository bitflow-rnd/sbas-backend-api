package org.sbas.entities.base

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import org.sbas.entities.CommonEntity
import org.sbas.entities.StringPrefixedSequenceIdGenerator
import java.io.Serializable
import javax.persistence.*

/**
 * 첨부 파일 정보
 */
@Entity
@Table(name = "base_attc")
class BaseAttc(

    @Id
    @Column(name = "attc_grp_id", nullable = false, length = 10)
    var attcGrpId: String? = null, // 첨부파일 그룹 ID

    @Id
    @GenericGenerator(
        name = "base_attc_id_seq",
        strategy = "org.sbas.entities.StringPrefixedSequenceIdGenerator",
        parameters = [
            Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "AT"),
            Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%010d"),
            Parameter(name = StringPrefixedSequenceIdGenerator.incrementSize, value = "1")
        ]) // AT0000000001
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "base_attc_id_seq")
    @Column(name = "attc_id", nullable = false, length = 12)
    var attcId: String? = null, // 첨부파일 ID

    @Column(name = "attc_dt", nullable = false, length = 8)
    var attcDt: String? = null, // 첨부 날짜

    @Column(name = "attc_tm", nullable = false, length = 6)
    var attcTm: String? = null, // 첨부 시간

    @Column(name = "file_type_cd", nullable = false, length = 8)
    var fileTypeCd: String? = null, // 파일 형식 코드
    
    @Column(name = "file_nm", nullable = false, length = 100)
    var fileNm: String? = null, // 파일명

    @Column(name = "locl_path", nullable = false, length = 200)
    var loclPath: String? = null, // 로컬 저장 경로

    @Column(name = "uri_path", nullable = false, length = 100)
    var uriPath: String? = null, // uri 경로

    @Column(name = "priv_yn", nullable = false)
    var privYn: String? = null,

    @Column(name = "rmk", length = 200)
    var rmk: String? = null, // 비고

) : CommonEntity(), Serializable {
    companion object {
        private const val serialVersionUID = -7627032719191928277L
    }
}


