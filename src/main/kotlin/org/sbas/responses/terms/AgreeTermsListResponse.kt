package org.sbas.responses.terms

import java.time.Instant

data class AgreeTermsListResponse (
    val userId: String,
    val termsType: String,
    val termsName: String,
    val recentYn: String,
    val detail: String,
    val agreeDttm: Instant?,
)
