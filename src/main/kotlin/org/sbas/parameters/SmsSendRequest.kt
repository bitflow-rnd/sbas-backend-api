package org.sbas.parameters

data class SmsSendRequest (
    var to: String? = null,
)

data class CheckCertNoRequest (
    var phoneNo: String,
    var certNo: String,
)