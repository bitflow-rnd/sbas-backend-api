package org.sbas.restparameters

import org.sbas.restresponses.NaverSmsResMsgs

data class NaverSmsMsgApiParams (

    var type: String,
    var countryCode: String,
    var from: String,
    var subject: String,
    var contentType: String,
    var content: String,
    var reserveTime: String,
    var reserveTimeZone: String,
    var scheduleCode: String,
    var messages: List<NaverSmsResMsgs>,
    var files: List<NaverSmsReqFiles>
)

data class NaverSmsReqMsgs(
    var subject: String,
    var content: String,
    var to: String
)

data class NaverSmsReqFiles(
    var name: String,
    var body: String
)
