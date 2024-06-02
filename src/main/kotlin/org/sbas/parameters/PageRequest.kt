package org.sbas.parameters

import jakarta.ws.rs.QueryParam
import org.sbas.utils.annotation.NoArg

@NoArg
data class PageRequest (
    @field: QueryParam("page") var page: Int?,
    @field: QueryParam("size") var size: Int?,
)