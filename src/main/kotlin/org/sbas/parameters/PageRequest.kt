package org.sbas.parameters

import org.sbas.utils.NoArg

@NoArg
data class PageRequest (
    var page: Int?,
    var size: Int?,
)