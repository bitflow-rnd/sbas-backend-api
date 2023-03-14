package org.sbas.entities.base

import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Embeddable
class BaseCodeEgenId : Serializable {
    @Size(max = 10)
    @NotNull
    @Column(name = "cm_mid", nullable = false, length = 10)
    var cmMid: String? = null // 대분류 코드

    @Size(max = 10)
    @NotNull
    @Column(name = "cm_sid", nullable = false, length = 10)
    var cmSid: String? = null // 소분류 코드

    override fun hashCode(): Int = Objects.hash(cmMid, cmSid)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as BaseCodeEgenId

        return cmMid == other.cmMid &&
                cmSid == other.cmSid
    }

    companion object {
        private const val serialVersionUID = 2195176205956635596L
    }
}