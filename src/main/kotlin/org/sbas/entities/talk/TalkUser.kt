package org.sbas.entities.talk

import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "talk_user")
class TalkUser {
    @EmbeddedId
    var id: TalkUserId? = null

    @Size(max = 1)
    @NotNull
    @Column(name = "host_yn", nullable = false, length = 1)
    var hostYn: String? = null // 호스트 여부

    @Size(max = 8)
    @NotNull
    @Column(name = "join_dt", nullable = false, length = 8)
    var joinDt: String? = null // 참여 일자

    @Size(max = 6)
    @NotNull
    @Column(name = "join_tm", nullable = false, length = 6)
    var joinTm: String? = null // 참여 시간

    @Size(max = 8)
    @Column(name = "wtdr_dt", length = 8)
    var wtdrDt: String? = null // 탈퇴 일자

    @Size(max = 6)
    @Column(name = "wtdr_tm", length = 6)
    var wtdrTm: String? = null // 탈퇴 시간

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