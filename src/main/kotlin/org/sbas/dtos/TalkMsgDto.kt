package org.sbas.dtos

import org.sbas.entities.talk.TalkRoom
import org.sbas.utils.StringUtils
import java.time.Instant

data class TalkMsgDto(
    var tkrmId: String? = null, // 대화방 ID
    var msgSeq: Long? = null, // 메시지 순번
    var msg: String? = null,
    var attcId: String? = null, // 첨부 ID
    var rgstUserId: String? = null, // 등록 사용자 ID
    var rgstDttm: Instant? = null, // 등록 일시
    var updtUserId: String? = null, // 수정 사용자 ID
    var updtDttm: Instant? = null, // 수정 일시
)

data class RegTalkRoomDto(
    var id: String, // 유저 ID
    var userId: String, // 초대받는 유저 ID
    var userNm: String,
) {
    fun toEntity(tkrmId: String): TalkRoom{
        return TalkRoom(
            tkrmId = tkrmId,
            tkrmNm = userNm,
            cretDt = StringUtils.getYyyyMmDd(),
            cretTm = StringUtils.getHhMmSs(),
            cretUserId = id,
        )
    }
}

data class RegGroupTalkRoomDto(
    var id: String, // 유저 ID
    var tkrmNm: String?, // 채팅방 이름
    var userIds: List<String>?, // 초대유저 ID
) {
    fun toEntity(tkrmId: String): TalkRoom {
        return TalkRoom(
            tkrmId = tkrmId,
            tkrmNm = tkrmNm,
            cretDt = StringUtils.getYyyyMmDd(),
            cretTm = StringUtils.getHhMmSs(),
            cretUserId = id,
        )
    }
}