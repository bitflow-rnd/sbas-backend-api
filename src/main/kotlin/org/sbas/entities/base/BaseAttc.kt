package org.sbas.entities.base

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Serializable
@Entity
@Table(name = "base_attc")
class BaseAttc(
    @EmbeddedId
    var id: BaseAttcId? = null,

    @Column(name = "attc_dt", nullable = false, length = 8)
    var attcDt: String? = null,

    @Column(name = "attc_tm", nullable = false, length = 6)
    var attcTm: String? = null,

    @Column(name = "file_type_cd", nullable = false, length = 8)
    var fileTypeCd: String? = null,

    @Column(name = "path_str", nullable = false, length = 100)
    var pathStr: String? = null,

    @Column(name = "rmk", length = 200)
    var rmk: String? = null,

) : CommonEntity(), java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = 8779842266363259071L
    }
}