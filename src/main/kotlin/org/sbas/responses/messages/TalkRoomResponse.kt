package org.sbas.responses.messages

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
        val msg = item.msg
        result += "{\"tkrmId\":\"${item.tkrmId}\", \"tkrmNm\":\"${item.tkrmNm}\", " +
            "\"msg\": \"${msg?.replace("\n", "\\n")}\", \"rgstDttm\":\"${item.rgstDttm}\"}"
        if(item != arrayData.last()){
            result += ","
        }
    }

    result += "]"

    return result
}

data class FileResponse(
    var fileTypeCd: String? = null,
    var file: String? = null,
)