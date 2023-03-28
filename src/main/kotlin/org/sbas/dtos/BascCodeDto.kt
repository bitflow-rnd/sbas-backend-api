package org.sbas.dtos

import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeId
import org.sbas.utils.NoArg

@NoArg
data class BascCodeSaveReq(
    var cdGrpId: String,
    var cdGrpNm: String,
    var cdId: String,
    var cdNm: String,
    var cdVal: String,
    var cdSeq: Long,
    var rmk: String,
)

fun BascCodeSaveReq.toEntity(): BaseCode {
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

@NoArg
data class BaseCodeUpdateReq(
    var cdGrpId: String,
    var cdId: String,
    var cdNm: String,
    var cdVal: String,
    var cdSeq: Long,
    var rmk: String,
)

fun BaseCodeUpdateReq.getId(): BaseCodeId {
    return BaseCodeId(cdGrpId = cdGrpId, cdId = cdId)
}

fun BaseCodeUpdateReq.update(entity: BaseCode): BaseCodeId {
    entity.cdNm = this.cdNm
    entity.cdVal = this.cdVal
    entity.cdSeq = this.cdSeq
    entity.rmk = this.rmk
    entity.updtUserId = "ADMIN"
    return entity.id
}
