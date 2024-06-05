package org.sbas.dtos

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeId

/**
 * 공통코드 그룹 등록 Dto
 */
data class BaseCodeGrpSaveReq(
    @field: [NotBlank(message = "코드 그룹 번호는 필수 값입니다.") Length(max = 6, message = "최대 6자리입니다.")]
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
data class BaseCodeSaveReq(
    @field: [NotBlank(message = "코드 그룹 번호는 필수 값입니다.") Length(max = 6, message = "최대 6자리입니다.")]
    val cdGrpId: String,
    val cdGrpNm: String?,
    @field: [NotBlank(message = "코드번호는 필수 값입니다.") Length(max = 8, message = "최대 8자리입니다.")]
    val cdId: String,
    val cdNm: String?,
    @field: Min(value = 1)
    val cdSeq: Int?,
    val cdVal: String?,
    val rmk: String?,
) {
    fun toEntity(): BaseCode {
        val baseCodeId = BaseCodeId(cdGrpId = this.cdGrpId, cdId = this.cdId)
        return BaseCode(
            id = baseCodeId,
            cdGrpNm = cdGrpNm,
            cdNm = cdNm,
            cdSeq = cdSeq,
            cdVal = cdVal,
            rmk = rmk,
        )
    }
}

/**
 * 공통코드 그룹 수정 Dto
 */
data class BaseCodeGrpUpdateReq(
    @field: [NotBlank(message = "코드 그룹 번호는 필수 값입니다.") Length(max = 6, message = "최대 6자리입니다.")]
    var cdGrpId: String,
    var cdGrpNm: String?,
    var rmk: String?,
)

/**
 * 공통코드 수정 Dto
 */
data class BaseCodeUpdateReq(
    @field: [NotBlank(message = "코드 그룹 번호는 필수 값입니다.") Length(max = 6, message = "최대 6자리입니다.")]
    var cdGrpId: String,
    var cdGrpNm: String?,
    @field: [NotBlank(message = "코드번호는 필수 값입니다.") Length(max = 8, message = "최대 8자리입니다.")]
    var cdId: String,
    var cdNm: String?,
    var cdVal: String?,
    @field: Min(value = 1)
    var cdSeq: Int?,
    var rmk: String?,
)

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