package org.sbas.responses

import org.sbas.entities.info.InfoUser

data class InfoUserListResponse (
        override var result: List<InfoUser>? = null
): AbstractResponse<List<InfoUser>?>()