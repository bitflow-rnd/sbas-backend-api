package org.sbas.dtos

import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeId
import org.sbas.utils.NoArg

@NoArg
data class BaseCodeDto(
    var cdGrpId: String,
    var cdGrpNm: String,
    var cdId: String,
    var cdNm: String,
    var cdVal: String,
    var cdSeq: Long,
    var rmk: String,
)

fun BaseCodeDto.toEntity(): BaseCode {
    val baseCodeId = BaseCodeId(cdGrpId = this.cdGrpId, cdId = this.cdId)
    val entity = BaseCode(
        id = baseCodeId,
        cdGrpNm = this.cdGrpNm,
        cdNm = this.cdNm,
        cdVal = this.cdVal,
        cdSeq = this.cdSeq,
        rmk = this.rmk,
    )
    entity.rgstUserId = "ADMIN"
    entity.updtUserId = "ADMIN"
    return entity

}