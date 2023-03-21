package org.sbas.response

import org.sbas.entities.info.InfoUser
import java.util.stream.Stream

data class UserInfoListResponse (
        override var result: List<InfoUser>? = null
): AbstractResponse<List<InfoUser>?>()