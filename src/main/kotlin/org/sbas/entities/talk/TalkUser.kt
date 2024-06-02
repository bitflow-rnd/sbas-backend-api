package org.sbas.entities.talk

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.sbas.entities.CommonEntity
import java.io.Serializable

/**
 * 대화 멤버 정보
 */
@Entity
@Table(name = "talk_user")
class TalkUser(
    @EmbeddedId
    var id: TalkUserId? = null,

    @Size(max = 1)
    @NotNull
    @Column(name = "host_yn", nullable = false, length = 1)
    var hostYn: String? = null, // 호스트 여부

    @Size(max = 8)
    @NotNull
    @Column(name = "join_dt", nullable = false, length = 8)
    var joinDt: String? = null, // 참여 일자

    @Size(max = 6)
    @NotNull
    @Column(name = "join_tm", nullable = false, length = 6)
    var joinTm: String? = null, // 참여 시간

    @Size(max = 8)
    @Column(name = "wtdr_dt", length = 8)
    var wtdrDt: String? = null, // 탈퇴 일자

    @Size(max = 6)
    @Column(name = "wtdr_tm", length = 6)
    var wtdrTm: String? = null, // 탈퇴 시간
) : CommonEntity()

@Embeddable
data class TalkUserId(
    @Size(max = 10)
    @NotNull
    @Column(name = "tkrm_id", nullable = false, length = 10)
    var tkrmId: String? = null, // 대화방 ID

    @Size(max = 10)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 10)
    var userId: String? = null, // 사용자 ID
) : Serializable {

    companion object {
        private const val serialVersionUID = -1503013811160659182L
    }
}