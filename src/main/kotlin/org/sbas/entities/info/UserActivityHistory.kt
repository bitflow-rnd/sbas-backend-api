package org.sbas.entities.info

import jakarta.persistence.*
import org.sbas.entities.CommonEntity
import java.io.Serial
import java.io.Serializable

@Entity
@Table(name = "user_activity_history")
class UserActivityHistory(
    @Id
    @EmbeddedId
    val id: UserActivityHistoryId,

    @Column(name = "activity_detail")
    var activityDetail: String?,
) : CommonEntity()

@Embeddable
data class UserActivityHistoryId(
    @Column(name = "pt_id", nullable = false)
    val ptId: String,

    @Column(name = "user_id", nullable = false)
    val userId: String,
) : Serializable {
    companion object {
        @Serial
        private const val serialVersionUID: Long = -7259175392834588967L
    }
}