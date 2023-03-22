package org.sbas.responses

import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.sbas.restresponses.EgenCodeMastApiResponse.CodeMastBody.CodeMastItems.CodeMastItem

@Schema(description = "공통코드 응답")
data class EgenCodeMastResponse (
    override var result: List<CodeMastItem>? = null
): AbstractResponse<List<CodeMastItem>?>()