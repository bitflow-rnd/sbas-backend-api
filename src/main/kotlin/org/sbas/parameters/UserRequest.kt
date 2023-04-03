package org.sbas.parameters

import com.fasterxml.jackson.annotation.JsonProperty

data class UserRequest (
    @JsonProperty("id")
    var id : String,
    @JsonProperty("adminId")
    var adminId : String? = null,
    @JsonProperty("isApproved")
    var isApproved : Boolean,
)

data class UserIdRequest(
    @JsonProperty("id")
    var id: String,
    @JsonProperty("adminId")
    var adminId : String? = null
)

data class ModifyPwRequest (
    @JsonProperty("id")
    var id: String,
    @JsonProperty("modifyPw")
    var modifyPw: String,
)

data class ModifyTelnoRequest (
    var id: String,
    var modifyTelno: String,
)

data class LoginRequest (
    @JsonProperty("id")
    var id: String,
    @JsonProperty("pw")
    var pw: String,
)