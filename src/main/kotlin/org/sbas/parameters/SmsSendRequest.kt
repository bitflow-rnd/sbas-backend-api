package org.sbas.parameters

import jakarta.validation.constraints.NotBlank

data class SmsSendRequest (
    @field: NotBlank
    val to: String,
)

data class CheckCertNoRequest (
    @field: NotBlank
    val phoneNo: String,
    @field: NotBlank
    val certNo: String,
)