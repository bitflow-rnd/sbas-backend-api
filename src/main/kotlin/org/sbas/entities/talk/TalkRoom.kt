package org.sbas.entities.talk

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.sbas.entities.CommonEntity

/**
 * 대화방 정보
 */
@Entity
@Table(name = "talk_room")
class TalkRoom(
    @Id
    @Column(name = "tkrm_id", nullable = false, length = 10)
    var tkrmId: String, // 대화방 ID

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
