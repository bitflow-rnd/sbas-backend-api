package org.sbas.entities.base

import kotlinx.serialization.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Serializable
@Embeddable
class BaseAttrId(
    @Column(name = "etty_cd", nullable = false, length = 8)
    var ettyCd: String? = null,

    @Column(name = "attr_id", nullable = false, length = 20)
    var attrId: String? = null,

    @Column(name = "app_strt_dt", nullable = false, length = 8)
    var appStrtDt: String? = null,
) : java.io.Serializable {

    companion object {
        private const val serialVersionUID = -7627032742449928277L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseAttrId) return false

        if (ettyCd != other.ettyCd) return false
        if (attrId != other.attrId) return false
        if (appStrtDt != other.appStrtDt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ettyCd?.hashCode() ?: 0
        result = 31 * result + (attrId?.hashCode() ?: 0)
        result = 31 * result + (appStrtDt?.hashCode() ?: 0)
        return result
    }
}