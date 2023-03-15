package org.sbas.entities.base

import org.sbas.entities.CommonEntity
import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.*

/**
 * 첨부 파일 정보
 */
@Entity
@Table(name = "base_attc")
class BaseAttc(
    @EmbeddedId
    var id: BaseAttcId? = null,

    @Column(name = "attc_dt", nullable = false, length = 8)
    var attcDt: String? = null, // 첨부 날짜

    @Column(name = "attc_tm", nullable = false, length = 6)
    var attcTm: String? = null, // 첨부 시간

    @Column(name = "file_type_cd", nullable = false, length = 8)
    var fileTypeCd: String? = null, // 파일 형식 코드

    @Column(name = "path_str", nullable = false, length = 100)
    var pathStr: String? = null, // 경로 문자열

    @Column(name = "rmk", length = 200)
    var rmk: String? = null, // 비고

) : CommonEntity()

@Embeddable
data class BaseAttcId(
    @Column(name = "attc_id", nullable = false, length = 10)
    var attcId: String? = null, // 첨부 ID

    @Column(name = "attc_seq", nullable = false, precision = 10)
    var attcSeq: BigDecimal? = null, // 첨부 순번

) : Serializable {

    companion object {
        private const val serialVersionUID: Long = -2680113473810765109L
    }
}