package org.sbas.entities.info

import org.sbas.entities.CommonEntity
import jakarta.persistence.*

@Entity
@Table(name = "user_fcm_token")
class UserFcmToken(
    @Id
    @Column(name = "fcm_token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var fcmTokenId: Long = 0L,

    @Column(name = "user_id", nullable = false)
    var userId: String,

    @Column(name = "registration_token", nullable = false)
    var reregistrationToken: String,

    @Column(name = "is_valid", nullable = false)
    var isValid: Boolean = true,
) : CommonEntity() {

    fun invalidateToken() {
        if (isValid) this.isValid = false
    }

}