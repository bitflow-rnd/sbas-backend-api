package org.sbas.responses.messages

import java.time.Instant

data class TalkRoomResponse(
    var tkrmId: String? = null,
    var tkrmNm: String? = null,
    var msg: String? = null,
    var rgstDttm: Instant? = null
)