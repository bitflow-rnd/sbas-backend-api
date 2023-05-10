package org.sbas.dtos

import org.sbas.utils.NoArg

@NoArg
data class PagingListDto(
    var totalCnt: Long,
    var items: MutableList<*>,
)