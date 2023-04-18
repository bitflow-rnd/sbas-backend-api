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
    var cdSeq: Long,
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
    var cdSeq: Long,
    var rmk: String,
)

fun BaseCodeUpdateReq.getId(): BaseCodeId {
    return BaseCodeId(cdGrpId = this.cdGrpId, cdId = this.cdId)
}