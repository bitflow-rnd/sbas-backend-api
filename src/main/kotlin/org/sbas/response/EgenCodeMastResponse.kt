package org.sbas.response

import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.sbas.restresponses.EgenCodeMastItem

@Schema(description = "공통코드 응답")
data class EgenCodeMastResponse (
    override var result: List<EgenCodeMastItem>? = null
): AbstractResponse<List<EgenCodeMastItem>?>()