package org.sbas.entities.info

import kotlinx.serialization.Serializable
import org.sbas.entities.CommonEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Serializable
@Entity
@Table(name = "info_hosp")
class InfoHosp(
    @Id
    @Column(name = "hosp_id", nullable = false, length = 10)
    var id: String? = null,

    @Column(name = "hosp_id_egen", length = 10)
    var hospIdEgen: String? = null,

    @Column(name = "attr_id", nullable = false, length = 10)
    var attrId: String? = null,

    @Column(name = "attr_val", length = 200)
    var attrVal: String? = null,

    @Column(name = "rmk", length = 200)
    var rmk: String? = null,
) : CommonEntity(), java.io.Serializable{
    companion object {
        private const val serialVersionUID: Long = -1836391687655606445L
    }
}