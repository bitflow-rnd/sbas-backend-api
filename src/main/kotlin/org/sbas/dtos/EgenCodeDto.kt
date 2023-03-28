package org.sbas.dtos

import org.sbas.entities.base.BaseCodeEgen
import org.sbas.entities.base.BaseCodeEgenId
import org.sbas.utils.NoArg

@NoArg
data class BaseCodeEgenSaveReq (
    var cmMid: String,
    var cmMnm: String,
    var cmSid: String,
    var cmSnm: String,
)

fun BaseCodeEgenSaveReq.toEntity(): BaseCodeEgen {
    val entity = BaseCodeEgen(
        id = BaseCodeEgenId(cmMid = this.cmMid, cmSid = this.cmSid),
        cmMnm = this.cmMnm,
        cmSnm = this.cmSnm,
    )
    entity.rgstUserId = "ADMIN"
    entity.updtUserId = "ADMIN"
    return entity
}