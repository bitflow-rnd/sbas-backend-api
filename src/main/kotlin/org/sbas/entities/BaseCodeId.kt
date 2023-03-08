package org.sbas.entities

import javax.persistence.Column
import javax.persistence.Embeddable
import kotlinx.serialization.Serializable


@Embeddable
@Serializable
class BaseCodeId (
    @Column(name = "cd_grp_id", nullable = false, length = 8)
    var cdGrpId: String? = null,

    @Column(name = "cd_id", nullable = false, length = 8)
    var cdId: String? = null
) : java.io.Serializable {
    companion object {
        private const val serialVersionUID = -165979700881194617L
    }
}