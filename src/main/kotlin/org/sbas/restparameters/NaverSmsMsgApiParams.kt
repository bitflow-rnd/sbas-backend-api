package org.sbas.restparameters

import jakarta.validation.constraints.NotNull

data class NaverSmsMsgApiParams(

        @field: NotNull (message = "메시지 타입이 누락되었습니다 (SMS, LMS, MMS)")
        var type: String,

        var countryCode: String?,

        @field: NotNull (message = "발신번호가 누락되었습니다")
        var from: String,

        var subject: String?,

        var contentType: String?,

        @field: NotNull (message = "기본 메시지 내용이 누락되었습니다")
        var content: String,

        var reserveTime: String?,

        var reserveTimeZone: String?,

        var scheduleCode: String?,

        @field: NotNull (message = "메시지 수신번호 목록이 누락되었습니다")
        var messages: List<NaverSmsReqMsgs>,

        var files: List<NaverSmsReqFiles>?

)

data class NaverSmsReqMsgs(
    var subject: String?,
    var content: String?,
    @field: NotNull (message = "메시지 수신번호가 누락되었습니다.")
    var to: String
)

data class NaverSmsReqFiles(
    var name: String?,
    var body: String?
)
