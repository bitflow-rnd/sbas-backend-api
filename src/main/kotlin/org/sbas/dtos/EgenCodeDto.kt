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
    return BaseCodeEgen(
        id = BaseCodeEgenId(cmMid = cmMid, cmSid = cmSid),
        cmMnm = cmMnm,
        cmSnm = cmSnm,
    )
}