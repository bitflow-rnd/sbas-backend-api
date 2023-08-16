package org.sbas.parameters

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

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