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
    var attcGrpId: String, // 첨부파일 그룹 ID

    @Column(name = "attc_dt", nullable = false, length = 8)
    var attcDt: String, // 첨부 날짜

    @Column(name = "attc_tm", nullable = false, length = 6)
    var attcTm: String, // 첨부 시간

    @Column(name = "file_type_cd", nullable = false, length = 8)
    var fileTypeCd: String, // 파일 형식 코드
    
    @Column(name = "file_nm", nullable = false, length = 100)
    var fileNm: String, // 파일명

    @Column(name = "locl_path", nullable = false, length = 200)
    var loclPath: String, // 로컬 저장 경로

    @Column(name = "uri_path", nullable = false, length = 100)
    var uriPath: String, // uri 경로

    @Column(name = "priv_yn", nullable = false)
    var privYn: String,

    @Column(name = "rmk", length = 200)
    var rmk: String? = null, // 비고
) : CommonEntity(), Serializable {

    companion object {
        private const val serialVersionUID = -7627032719191928277L
    }

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
    var attcId: String = ""
        protected set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseAttc) return false

        if (attcGrpId != other.attcGrpId) return false
        if (attcDt != other.attcDt) return false
        if (attcTm != other.attcTm) return false
        if (fileTypeCd != other.fileTypeCd) return false
        if (fileNm != other.fileNm) return false
        if (loclPath != other.loclPath) return false
        if (uriPath != other.uriPath) return false
        if (privYn != other.privYn) return false
        if (rmk != other.rmk) return false
        return attcId == other.attcId
    }

    override fun hashCode(): Int {
        var result = attcGrpId.hashCode()
        result = 31 * result + attcDt.hashCode()
        result = 31 * result + attcTm.hashCode()
        result = 31 * result + fileTypeCd.hashCode()
        result = 31 * result + fileNm.hashCode()
        result = 31 * result + loclPath.hashCode()
        result = 31 * result + uriPath.hashCode()
        result = 31 * result + privYn.hashCode()
        result = 31 * result + (rmk?.hashCode() ?: 0)
        result = 31 * result + attcId.hashCode()
        return result
    }

}


