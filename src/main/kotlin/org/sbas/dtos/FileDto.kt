package org.sbas.dtos

import org.sbas.entities.base.BaseAttc
import org.sbas.utils.NoArg
import org.sbas.utils.StringUtils

@NoArg
data class FileDto(
    val fileName: String,
    val localPath: String,
    val uriPath: String
) {

    fun toPublicEntity(attcGrpId: String, fileTypeCd: String, rmk: String?): BaseAttc {
        return BaseAttc(
            attcGrpId = attcGrpId,
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

    fun toPrivateEntity(attcGrpId: String, fileTypeCd: String, rmk: String?): BaseAttc {
        return BaseAttc(
            attcGrpId = attcGrpId,
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
}
