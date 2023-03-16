package org.sbas.restresponses

data class NaverSmsMsgApiResponse(
    var statusCode: String,
    var statusName: String,
    var messages: List<NaverSmsResMsgs>
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
