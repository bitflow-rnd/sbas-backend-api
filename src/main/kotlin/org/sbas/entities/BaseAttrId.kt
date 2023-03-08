package org.sbas.entities

import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity

@Embeddable
open class BaseAttrId : Serializable {
    @Column(name = "etty_cd", nullable = false, length = 8)
    open var ettyCd: String? = null

    @Column(name = "attr_id", nullable = false, length = 20)
    open var attrId: String? = null

    @Column(name = "app_strt_dt", nullable = false, length = 8)
    open var appStrtDt: String? = null

    override fun hashCode(): Int = Objects.hash(ettyCd, attrId, appStrtDt)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as BaseAttrId

        return ettyCd == other.ettyCd &&
                attrId == other.attrId &&
                appStrtDt == other.appStrtDt
    }

    companion object {
        private const val serialVersionUID = -7627032742449928277L
    }
}