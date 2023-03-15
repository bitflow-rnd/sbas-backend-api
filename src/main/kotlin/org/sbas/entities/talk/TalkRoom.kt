package org.sbas.entities.talk

import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "talk_room")
class TalkRoom {
    @EmbeddedId
    var id: TalkRoomId? = null

    @Size(max = 200)
    @NotNull
    @Column(name = "tkrm_nm", nullable = false, length = 200)
    var tkrmNm: String? = null // 대화방 이름

    @Size(max = 8)
    @NotNull
    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null // 이력 코드

    @Size(max = 8)
    @NotNull
    @Column(name = "cret_dt", nullable = false, length = 8)
    var cretDt: String? = null // 생성 날짜

    @Size(max = 6)
    @NotNull
    @Column(name = "cret_tm", nullable = false, length = 6)
    var cretTm: String? = null // 생성 시간

    @Size(max = 10)
    @NotNull
    @Column(name = "cret_user_id", nullable = false, length = 10)
    var cretUserId: String? = null

    @Size(max = 10)
    @NotNull
    @Column(name = "rgst_user_id", nullable = false, length = 10)
    var rgstUserId: String? = null

    @NotNull
    @Column(name = "rgst_dttm", nullable = false)
    var rgstDttm: Instant? = null

    @Size(max = 10)
    @NotNull
    @Column(name = "updt_user_id", nullable = false, length = 10)
    var updtUserId: String? = null

    @NotNull
    @Column(name = "updt_dttm", nullable = false)
    var updtDttm: Instant? = null
}