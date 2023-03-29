package org.sbas.parameters

import org.sbas.utils.NoArg

data class SmsSendRequest (
    var to: String? = null,
)

@NoArg
data class CheckCertNoRequest (
    var phoneNo: String,
    var certNo: String,
)