package org.sbas.utils

import jakarta.ws.rs.core.Response.StatusType
import java.io.Serial

class CustomizedException(
    override var message: String,
    var status: StatusType,
) : RuntimeException() {
    companion object {
        @Serial
        private const val serialVersionUID: Long = 8131583884372719252L
    }
}