package org.sbas.entities.talk

import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "talk_msg")
class TalkMsg {
    @EmbeddedId
    var id: TalkMsgId? = null

    @Size(max = 8)
    @NotNull
    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null

    @Size(max = 1000)
    @NotNull
    @Column(name = "msg", nullable = false, length = 1000)
    var msg: String? = null

    @Size(max = 10)
    @Column(name = "attc_id", length = 10)
    var attcId: String? = null

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