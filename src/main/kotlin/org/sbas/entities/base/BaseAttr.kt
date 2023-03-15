package org.sbas.entities.base

import org.sbas.entities.CommonEntity
import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.*

/**
 * 속성 정보
 */
@Entity
@Table(name = "base_attr")
class BaseAttr(
    @EmbeddedId
    var id: BaseAttrId? = null,

    @Column(name = "attr_lvl", nullable = false, precision = 1)
    var attrLvl: BigDecimal? = null, // 속성 수준

    @Column(name = "pr_attr_id", nullable = false, length = 20)
    var prAttrId: String? = null, // 부모 속성 ID

    @Column(name = "attr_nm", nullable = false, length = 100)
    var attrNm: String? = null, // 속성 이름

    @Column(name = "attr_seq", nullable = false, precision = 1)
    var attrSeq: BigDecimal? = null, // 속성 순번

    @Column(name = "attr_val", length = 200)
    var attrVal: String? = null, // 속성 값

    @Column(name = "val_type_cd", length = 8)
    var valTypeCd: String? = null, // 값 형식 코드

    @Column(name = "app_end_dt", nullable = false, length = 8)
    var appEndDt: String? = null, // 적용 종료 날짜

    @Column(name = "rmk", length = 200)
    var rmk: String? = null, // 비고

) : CommonEntity()

@Embeddable
data class BaseAttrId(
    @Column(name = "etty_cd", nullable = false, length = 8)
    var ettyCd: String? = null, // 엔티티 코드

    @Column(name = "attr_id", nullable = false, length = 20)
    var attrId: String? = null, // 속성 ID

    @Column(name = "app_strt_dt", nullable = false, length = 8)
    var appStrtDt: String? = null, // 적용 시작 날짜
) : Serializable {

    companion object {
        private const val serialVersionUID = -7627032742449928277L
    }
}

