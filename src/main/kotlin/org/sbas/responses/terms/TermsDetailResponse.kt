package org.sbas.responses.terms

data class TermsDetailResponse (
    val termsType: String,
    val termsName: String,
    val termsVersion: String,
    val detail: String,
    val effectiveDt: String,
)