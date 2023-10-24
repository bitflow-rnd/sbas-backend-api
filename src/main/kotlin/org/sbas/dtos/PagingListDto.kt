package org.sbas.dtos

data class PagingListDto(
    var totalCnt: Long,
    var items: List<*>,
)