package org.sbas.responses.messages

import io.quarkus.runtime.annotations.RegisterForReflection
import java.time.Instant

@RegisterForReflection
data class TalkRoomResponse(
    var tkrmId: String? = null,
    var tkrmNm: String? = null,
    var msg: String? = null,
    var rgstDttm: Instant? = null
)