package org.sbas.utils

import java.io.Serial
import jakarta.ws.rs.core.Response.StatusType

class CustomizedException(
    override var message: String,
    var status: StatusType,
) : RuntimeException() {
    companion object {
        @Serial
        private const val serialVersionUID: Long = 8131583884372719252L
    }
}