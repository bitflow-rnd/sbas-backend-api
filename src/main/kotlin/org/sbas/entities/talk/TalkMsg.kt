package org.sbas.entities.talk

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.sbas.dtos.TalkMsgDto
import java.io.Serializable
import java.time.Instant
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * 대화 메시지
 */
@Entity
@Table(name = "talk_msg")
class TalkMsg(
    @EmbeddedId
    var id: TalkMsgId? = null,

    @Size(max = 8)
    @NotNull
    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null, // 이력 코드

    @Size(max = 1000)
    @NotNull
    @Column(name = "msg", nullable = false, length = 1000)
    var msg: String? = null, // 이력 순번

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

    @NotNull
    @Column(name = "hist_seq", nullable = false, precision = 3)
    var histSeq: Long? = null, // 이력 순번
) : Serializable {

    companion object {
        private const val serialVersionUID = -544776586376217282L
    }
}

fun arrToJson(arrayData: MutableList<TalkMsgDto>): String {
    var result = "["

    for (item in arrayData) {
        val attcId = item.attcId
        result += "{\"id\":{\"tkrmId\":\"${item.tkrmId}\", \"msgSeq\":${item.msgSeq}, \"histSeq\":${item.histSeq}}, " +
            "\"histCd\":\"${item.histCd}\", \"msg\":\"${item.msg}\", \"attcId\":${attcId?.let { "\"$it\"" } ?: "null"}, \"rgstUserId\":\"${item.rgstUserId}\"," +
            "\"rgstDttm\":\"${item.rgstDttm}\", \"updtUserId\":\"${item.updtUserId}\", \"updtDttm\":\"${item.updtDttm}\"}"
        if(item != arrayData.last()){
            result += ","
        }
    }

    result += "]"

    return result
}