package org.sbas.entities.base

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import org.sbas.serializers.BigDecimalSerializer
import java.math.BigDecimal
import javax.persistence.*

@Serializable
@Entity
@Table(name = "base_attr")
class BaseAttr(
    @EmbeddedId
    var id: BaseAttrId? = null,

    @Column(name = "attr_lvl", nullable = false, precision = 1)
    @Serializable(with = BigDecimalSerializer::class)
    var attrLvl: BigDecimal? = null,

    @Column(name = "pr_attr_id", nullable = false, length = 20)
    var prAttrId: String? = null,

    @Column(name = "attr_nm", nullable = false, length = 100)
    var attrNm: String? = null,

    @Column(name = "attr_seq", nullable = false, precision = 1)
    @Serializable(with = BigDecimalSerializer::class)
    var attrSeq: BigDecimal? = null,

    @Column(name = "attr_val", length = 200)
    var attrVal: String? = null,

    @Column(name = "val_type_cd", length = 8)
    var valTypeCd: String? = null,

    @Column(name = "app_end_dt", nullable = false, length = 8)
    var appEndDt: String? = null,

    @Column(name = "rmk", length = 200)
    var rmk: String? = null,

) : CommonEntity(), java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = 937111344016409773L
    }
}

