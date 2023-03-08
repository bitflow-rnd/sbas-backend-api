package org.sbas.response

import kotlinx.serialization.Serializable
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.sbas.entities.BaseCodeId

//@Schema(description = "공통코드 응답")
@Serializable
data class BaseCodeResponse (

    var id: BaseCodeId? = null,
    var cdGrpNm: String? = null,
    var cdNm: String? = null,
    var cdVal: String? = null,
    var cdSeq: Long? = null,
    var rmk: String? = null,
    var rgstUserId: String? = null,
    @Schema(description = "등록일", example = "2023.02.03 11:22", maxLength = 16, format = "yyyy.MM.dd HH:mm")
    var rgstDttm: String? = null,
    var updtUserId: String? = null,
    @Schema(description = "수정일", example = "2023.02.03 11:22", maxLength = 16, format = "yyyy.MM.dd HH:mm")
    var updtDttm: String? = null

) : java.io.Serializable {
    companion object {
        private val serialVersionUID = 165979701881194617L
    }
}
