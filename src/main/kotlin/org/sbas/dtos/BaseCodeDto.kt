package org.sbas.dtos

import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeId
import org.sbas.utils.NoArg
import javax.validation.constraints.NotBlank

/**
 * 공통코드 등록 Dto
 */
@NoArg
data class BaseCodeSaveReq(
    @field: NotBlank(message = "코드 그룹 ID는 필수 값입니다.")
    var cdGrpId: String,
    var cdGrpNm: String,
    var cdId: String,
    var cdNm: String,
    var cdSeq: Int,
)

fun BaseCodeSaveReq.toCdIdEntity(): BaseCode {
    val baseCodeId = BaseCodeId(cdGrpId = this.cdGrpId, cdId = this.cdId)
    return BaseCode(
        id = baseCodeId,
        cdGrpNm = cdGrpNm,
        cdNm = cdNm,
        cdSeq = cdSeq,
    )
}

/**
 * 공통코드 수정 Dto
 */
@NoArg
data class BaseCodeUpdateReq(
    var cdGrpId: String,
    var cdGrpNm: String,
    var cdId: String,
    var cdNm: String,
    var cdVal: String,
    var cdSeq: Int,
    var rmk: String,
)

fun BaseCodeUpdateReq.getId(): BaseCodeId {
    return BaseCodeId(cdGrpId = this.cdGrpId, cdId = this.cdId)
}

@NoArg
data class SidoSiGunGuDto(
    var siDoCd: String? = null,
    var siGunGuCd: String? = null,
)

/**
 * 공통코드 응답 Dto
 */
data class BaseCodeResponse(
    val cdGrpId: String,
    val cdGrpNm: String?,
    val cdId: String,
    val cdNm: String?,
    val cdVal: String?,
    val cdSeq: Int?,
    val rmk: String?,
)