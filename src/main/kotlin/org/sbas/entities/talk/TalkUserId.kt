package org.sbas.entities.talk

import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Embeddable
class TalkUserId : Serializable {
    @Size(max = 10)
    @NotNull
    @Column(name = "tkrm_id", nullable = false, length = 10)
    var tkrmId: String? = null // 대화방 ID

    @Size(max = 10)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 10)
    var userId: String? = null // 사용자 ID

    override fun hashCode(): Int = Objects.hash(tkrmId, userId)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as TalkUserId

        return tkrmId == other.tkrmId &&
                userId == other.userId
    }

    companion object {
        private const val serialVersionUID = -1503013811160659182L
    }
}