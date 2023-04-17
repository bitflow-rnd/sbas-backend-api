package org.sbas.responses.messages

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Instant

data class TalkRoomResponse(
    var tkrmId: String? = null,
    var tkrmNm: String? = null,
    var msg: String? = null,
    var rgstDttm: Instant? = null
)

fun arrToJson(arrayData: MutableList<TalkRoomResponse>): String {
    var result = "["

    for (item in arrayData) {
        result += "{\"tkrmId\":${item.tkrmId}, \"tkrmNm\":${item.tkrmNm}, \"msg\":${item.msg}, \"rgstDttm\":${item.rgstDttm}}"
    }

    result += "]"

    return result
}