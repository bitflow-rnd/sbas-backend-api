package org.sbas.entities.info

import jakarta.persistence.*
import org.sbas.entities.CommonEntity

@Entity
@Table(name = "user_activity_history")
class UserActivityHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_activity_history_id", nullable = false)
    val id: Int = 0,

    @Column(name = "pt_id", nullable = false)
    val ptId: String,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "activity_detail")
    var activityDetail: String?,
) : CommonEntity()