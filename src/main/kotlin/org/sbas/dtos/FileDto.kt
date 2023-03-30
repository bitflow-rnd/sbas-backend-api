package org.sbas.dtos

import org.sbas.entities.base.BaseAttc
import org.sbas.utils.NoArg
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@NoArg
data class FileDto(
    var filename: String,
    var localPath: String,
    var uriPath: String
)

fun FileDto.toEntity(fileTypeCd: String, rmk: String?): BaseAttc {
    val now = LocalDateTime.now()

    val entity = BaseAttc(
        attcDt = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
        attcTm = now.format(DateTimeFormatter.ofPattern("HHmmss")),
        fileTypeCd = fileTypeCd,
        fileNm = filename,
        loclPath = localPath,
        uriPath = uriPath,
        rmk = rmk,
    )
    entity.rgstUserId = "admin"
    entity.updtUserId = "admin"

    return entity
}