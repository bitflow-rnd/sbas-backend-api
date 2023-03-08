package org.sbas.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.sbas.serializers.TimestampSerializer
import java.time.Instant

@Schema(description = "공통코드")
@Serializable
data class BaseCodeResponse (

    // type: "date" format: "yyyy-mm-dd"
    @Schema(description = "생년월일", example = "yyyy.MM.dd HH:mm", maxLength = 16, format = "yyyy.MM.dd HH:mm")
    var name: String
)
