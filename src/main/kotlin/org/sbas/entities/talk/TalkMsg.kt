package org.sbas.entities.talk

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.sbas.dtos.TalkMsgDto
import java.io.Serializable
import java.time.Instant

/**
 * 대화 메시지
 */
@Entity
@Table(name = "talk_msg")
class TalkMsg(
    @EmbeddedId
    var id: TalkMsgId? = null,

    @Size(max = 1000)
    @NotNull
    @Column(name = "msg", nullable = false, length = 1000)
    var msg: String? = null,

    @Size(max = 10)
    @Column(name = "attc_id", length = 10)
    var attcId: String? = null, // 첨부 ID

    @Column(name = "rgst_user_id", nullable = false, length = 15, updatable = false)
    var rgstUserId: String? = null, // 등록 사용자 ID

    @Column(name = "rgst_dttm", nullable = false, updatable = false)
    @CreationTimestamp
    var rgstDttm: Instant? = null, // 등록 일시

    @Column(name = "updt_user_id", nullable = false, length = 15)
    var updtUserId: String? = null, // 수정 사용자 ID

    @Column(name = "updt_dttm", nullable = false)
    @UpdateTimestamp
    var updtDttm: Instant? = null, // 수정 일시
)

@Embeddable
data class TalkMsgId(
    @Size(max = 10)
    @NotNull
    @Column(name = "tkrm_id", nullable = false, length = 10)
    var tkrmId: String? = null, // 대화방 ID

    @NotNull
    @Column(name = "msg_seq", nullable = false, precision = 3)
    var msgSeq: Long? = null, // 메시지 순번
) : Serializable {

    companion object {
        private const val serialVersionUID = -544776586376217282L
    }
}

fun arrToJson(arrayData: MutableList<TalkMsgDto>): String {
    var result = "["

    for (item in arrayData) {
        val attcId = item.attcId
        result += "{\"id\":{\"tkrmId\":\"${item.tkrmId}\", \"msgSeq\":${item.msgSeq}, " +
            "\"msg\":\"${item.msg}\", \"attcId\":${attcId?.let { "\"$it\"" } ?: "null"}, \"rgstUserId\":\"${item.rgstUserId}\"," +
            "\"rgstDttm\":\"${item.rgstDttm}\", \"updtUserId\":\"${item.updtUserId}\", \"updtDttm\":\"${item.updtDttm}\"}"
        if(item != arrayData.last()){
            result += ","
        }
    }

    result += "]"

    return result
}