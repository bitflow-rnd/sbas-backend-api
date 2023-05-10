package org.sbas.dtos

import org.sbas.entities.info.InfoCntc
import org.sbas.entities.info.InfoCntcId
import org.sbas.utils.NoArg

@NoArg
data class InfoCntcDto (
    var id: String,
    var cntcType: String,
    var mbrId: String,
)
fun InfoCntcDto.toEntity(histSeq: Int) : InfoCntc{
    val infoCntcId = InfoCntcId(
        userId = id,
        histCd = "1",
        histSeq = histSeq,
        mbrId = mbrId,
    )
    return InfoCntc(
        id = infoCntcId,
        cntcType = cntcType
    )
}