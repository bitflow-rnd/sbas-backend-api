package org.sbas.response

import org.eclipse.microprofile.openapi.annotations.media.Schema

@Schema(description = "스트링 응답")
data class StringResponse (
        override var result: String? = null
): AbstractResponse<String?>()