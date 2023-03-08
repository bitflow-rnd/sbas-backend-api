package org.sbas.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Serializer(forClass = Instant::class)
object TimestampSerializer: KSerializer<Instant> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
            .withZone(ZoneId.systemDefault())
        val ret = formatter.format(value)
        encoder.encodeString(ret.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        val theTime = LocalDateTime.parse(decoder.decodeString())
        val zdt: ZonedDateTime = theTime.atZone(ZoneId.systemDefault())
        return zdt.toInstant()
    }

}