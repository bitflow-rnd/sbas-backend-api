package org.sbas.entities.talk

import org.sbas.entities.CommonEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * 대화방 정보
 */
@Entity
@Table(name = "talk_room")
class TalkRoom(
    @Id
    @Column(name = "tkrm_id", nullable = false, length = 10)
    var tkrmId: String? = null, // 대화방 ID

    @Size(max = 200)
    @NotNull
    @Column(name = "tkrm_nm", nullable = false, length = 200)
    var tkrmNm: String? = null, // 대화방 이름

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