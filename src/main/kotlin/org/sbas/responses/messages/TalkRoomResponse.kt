package org.sbas.responses.messages

import io.quarkus.runtime.annotations.RegisterForReflection
import org.sbas.entities.talk.TalkMsg
import org.sbas.entities.talk.TalkRoom
import java.time.Instant

@RegisterForReflection
data class TalkRoomResponse(
    var tkrmId: String? = null,
    var tkrmNm: String? = null,
    var msg: String? = null,
    var rgstDttm: Instant? = null
)