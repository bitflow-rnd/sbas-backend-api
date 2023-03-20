package org.sbas.restresponses

import com.fasterxml.jackson.annotation.JsonProperty

data class NaverSmsMsgApiResponse(
        var statusCode: String? = null,
        var statusName: String? = null,
        var messages: List<NaverSmsResMsgs>? = null
)


data class NaverSmsResMsgs(
    var messageId: String,
    var requestTime: String,
    var from: String,
    var to: String,
    var contentType: String,
    var countryCode: String,
    var content: String,
    var completeTime: String,
    var status: String,
    var telcoCode: String,
    var statusName: String,
    var statusCode: String,
    var statusMessage: String
)
