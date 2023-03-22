package org.sbas.responses

import org.eclipse.microprofile.openapi.annotations.media.Schema

//@Schema(description = "공통코드 응답")
data class BaseCodeResponse (

    var cdGrpId: String? = null,
    var cdId: String? = null,
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

)
