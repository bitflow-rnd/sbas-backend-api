package org.sbas.dtos

import org.sbas.entities.base.BaseAttc
import org.sbas.utils.NoArg
import org.sbas.utils.StringUtils

@NoArg
data class FileDto(
    var fileName: String,
    var localPath: String,
    var uriPath: String
)

fun FileDto.toPublicEntity(fileTypeCd: String, rmk: String?): BaseAttc {
    return BaseAttc(
        attcDt = StringUtils.getYyyyMmDd(),
        attcTm = StringUtils.getHhMmSs(),
        fileTypeCd = fileTypeCd,
        fileNm = fileName,
        loclPath = localPath,
        uriPath = uriPath,
        privYn = "N",
        rmk = rmk,
    )
}

fun FileDto.toPrivateEntity(fileTypeCd: String, rmk: String?): BaseAttc {
    return BaseAttc(
        attcDt = StringUtils.getYyyyMmDd(),
        attcTm = StringUtils.getHhMmSs(),
        fileTypeCd = fileTypeCd,
        fileNm = fileName,
        loclPath = localPath,
        uriPath = uriPath,
        privYn = "Y",
        rmk = rmk,
    )
}