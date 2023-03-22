package org.sbas.responses

import org.sbas.entities.info.InfoUser

data class UserInfoListResponse (
        override var result: List<InfoUser>? = null
): AbstractResponse<List<InfoUser>?>()