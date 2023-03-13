package org.sbas.response

import kotlinx.serialization.Serializable
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.sbas.restresponses.EgenCodeMastItem

@Schema(description = "공통코드 응답")
@Serializable
data class EgenCodeMastResponse(
    var result: List<EgenCodeMastItem>?
) : java.io.Serializable {
    companion object {
        private val serialVersionUID = 165979143281194617L
    }
}