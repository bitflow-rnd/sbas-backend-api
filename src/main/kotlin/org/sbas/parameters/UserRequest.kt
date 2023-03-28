package org.sbas.parameters

import com.fasterxml.jackson.annotation.JsonProperty

data class UserRequest (
    @JsonProperty("id")
    var id: String,
    @JsonProperty("adminId")
    var adminId: String,
)

data class modifyPwRequest (
        @JsonProperty("id")
        var id: String,
        @JsonProperty("modifyPw")
        var modifyPw: String,
)