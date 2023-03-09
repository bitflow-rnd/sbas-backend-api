package org.sbas.entities.bdas

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import org.sbas.serializers.BigDecimalSerializer
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Serializable
@Entity
@Table(name = "bdas_symp")
class BdasSymp(
    @EmbeddedId
    var id: BdasSympId? = null,

    @Column(name = "hist_cd", length = 8)
    var histCd: String? = null,

    @Column(name = "hist_seq", precision = 3)
    @Serializable(with = BigDecimalSerializer::class)
    var histSeq: BigDecimal? = null,

    @Column(name = "attr_id", precision = 10)
    @Serializable(with = BigDecimalSerializer::class)
    var attrId: BigDecimal? = null,

    @Column(name = "attr_val")
    var attrVal: ByteArray? = null,

    @Column(name = "rmk", length = 50)
    var rmk: String? = null,

) :CommonEntity(), java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = 4964818982254350947L
    }
}