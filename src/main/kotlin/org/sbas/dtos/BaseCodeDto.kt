package org.sbas.dtos

import org.hibernate.validator.constraints.Length
import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeId
import org.sbas.utils.NoArg
import javax.validation.constraints.NotBlank

@NoArg
data class BaseCodeGrpSaveReq(
    @field: [NotBlank(message = "코드 그룹 번호는 필수 값입니다.") Length(max = 4, message = "최대 4자리입니다.")]
    val cdGrpId: String,
    val cdGrpNm: String?,
    val rmk: String?,
) {
    fun toEntity(): BaseCode {
        return BaseCode(
            id = BaseCodeId(cdGrpId = cdGrpId, cdId = cdGrpId),
            cdGrpNm = cdGrpNm,
            cdSeq = 0,
            rmk = rmk,
        )
    }
}

/**
 * 공통코드 등록 Dto
 */
@NoArg
data class BaseCodeSaveReq(
    @field: NotBlank(message = "코드 그룹 번호는 필수 값입니다.")
    var cdGrpId: String,
    var cdGrpNm: String,
    @field: [NotBlank(message = "코드번호는 필수 값입니다.") Length(max = 8, message = "최대 8자리입니다.")]
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
    var cdGrpNm: String?,
    var cdId: String,
    var cdNm: String?,
    var cdVal: String?,
    var cdSeq: Int?,
    var rmk: String?,
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