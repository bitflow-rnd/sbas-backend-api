package org.sbas.parameters

import org.sbas.utils.annotation.NoArg
import jakarta.ws.rs.QueryParam

@NoArg
data class PageRequest (
    @field: QueryParam("page") var page: Int?,
    @field: QueryParam("size") var size: Int?,
)