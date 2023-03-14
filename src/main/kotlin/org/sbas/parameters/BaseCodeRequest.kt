package org.sbas.parameters

import org.eclipse.microprofile.openapi.annotations.media.Schema

@Schema(description = "공통코드 응답")
data class BaseCodeRequest (

    var cdGrpId: String,
    var cdId: String

) : java.io.Serializable {
    companion object {
        private const val serialVersionUID = 165979702881194617L
    }
}
