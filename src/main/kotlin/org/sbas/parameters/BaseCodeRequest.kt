package org.sbas.parameters

import kotlinx.serialization.Serializable
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.sbas.entities.BaseCodeId
import org.sbas.response.AbstractResponse
import javax.persistence.Column

@Schema(description = "공통코드 응답")
@Serializable
data class BaseCodeRequest (

    var cdGrpId: String,
    var cdId: String

) : java.io.Serializable {
    companion object {
        private const val serialVersionUID = 165979702881194617L
    }
}
