package org.sbas.entities.bdas

import org.sbas.entities.CommonEntity
import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.*

/**
 * 증상 정보
 */
@Entity
@Table(name = "bdas_symp")
class BdasSymp(
    @EmbeddedId
    var id: BdasSympId? = null,

    @Column(name = "hist_cd", length = 8)
    var histCd: String? = null, // 이력 코드

    @Column(name = "attr_id", precision = 10)
    var attrId: BigDecimal? = null, // 속성 ID

    @Column(name = "attr_val")
    var attrVal: ByteArray? = null, // 속성 값

    @Column(name = "rmk", length = 50)
    var rmk: String? = null, // 비고

) :CommonEntity()

@Embeddable
data class BdasSympId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null, // 환자 ID

    @Column(name = "bdas_seq", nullable = false, precision = 10)
    var bdasSeq: BigDecimal? = null, // 병상 배정 순번

    @Column(name = "hist_seq", precision = 3)
    var histSeq: BigDecimal? = null, // 이력 순번
) : Serializable {

    companion object {
        private const val serialVersionUID = 5725666136275167985L
    }
}