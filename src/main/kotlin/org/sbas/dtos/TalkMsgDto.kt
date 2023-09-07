package org.sbas.dtos

import java.time.Instant

data class TalkMsgDto(
    var tkrmId: String? = null, // 대화방 ID
    var msgSeq: Long? = null, // 메시지 순번
    var histSeq: Long? = null, // 이력 순번
    var histCd: String? = null, // 이력 코드
    var msg: String? = null,
    var attcId: String? = null, // 첨부 ID
    var rgstUserId: String? = null, // 등록 사용자 ID
    var rgstDttm: Instant? = null, // 등록 일시
    var updtUserId: String? = null, // 수정 사용자 ID
    var updtDttm: Instant? = null, // 수정 일시
)