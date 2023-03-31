package org.sbas.utils

import javax.ws.rs.core.Response.StatusType

class CustomizedException(
    override var message: String,
    var status: StatusType,
) : RuntimeException()