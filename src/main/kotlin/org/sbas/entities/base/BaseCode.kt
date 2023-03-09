package org.sbas.entities.base

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import java.io.Serial
import javax.persistence.*

@Entity
@Table(name = "base_code")
@Serializable
class BaseCode(
    @EmbeddedId
    var id: BaseCodeId? = null,

    @Column(name = "cd_grp_nm", length = 100)
    var cdGrpNm: String? = null,

    @Column(name = "cd_nm", length = 100)
    var cdNm: String? = null,

    @Column(name = "cd_val", length = 200)
    var cdVal: String? = null,

    @Column(name = "cd_seq", precision = 3)
    var cdSeq: Long? = null,

    @Column(name = "rmk", length = 200)
    var rmk: String? = null,
) : CommonEntity(), java.io.Serializable {

    companion object {
        @Serial
        private const val serialVersionUID: Long = -6517953454760971895L
    }
}