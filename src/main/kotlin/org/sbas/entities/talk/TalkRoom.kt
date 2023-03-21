package org.sbas.entities.talk

import org.hibernate.Hibernate
import org.sbas.entities.CommonEntity
import java.io.Serializable
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * 대화방 정보
 */
@Entity
@Table(name = "talk_room")
class TalkRoom(
    @EmbeddedId
    var id: TalkRoomId? = null,

    @Size(max = 200)
    @NotNull
    @Column(name = "tkrm_nm", nullable = false, length = 200)
    var tkrmNm: String? = null, // 대화방 이름

    @Size(max = 8)
    @NotNull
    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null, // 이력 코드

    @Size(max = 8)
    @NotNull
    @Column(name = "cret_dt", nullable = false, length = 8)
    var cretDt: String? = null, // 생성 날짜

    @Size(max = 6)
    @NotNull
    @Column(name = "cret_tm", nullable = false, length = 6)
    var cretTm: String? = null, // 생성 시간

    @Size(max = 10)
    @NotNull
    @Column(name = "cret_user_id", nullable = false, length = 10)
    var cretUserId: String? = null,
) : CommonEntity()

@Embeddable
data class TalkRoomId(
    @Size(max = 10)
    @NotNull
    @Column(name = "tkrm_id", nullable = false, length = 10)
    var tkrmId: String? = null, // 대화방 ID

    @NotNull
    @Column(name = "hist_seq", nullable = false, precision = 3)
    var histSeq: BigDecimal? = null, // 이력 순번
) : Serializable {

    companion object {
        private const val serialVersionUID = -7947377909525848027L
    }
}