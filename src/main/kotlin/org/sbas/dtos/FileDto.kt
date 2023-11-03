package org.sbas.dtos

import org.sbas.entities.base.BaseAttc
import org.sbas.utils.StringUtils

data class FileDto(
    val fileName: String,
    val fileExt: String,
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

data class AttcIdResponse(
    val attcGrpId: String,
    val attcId: List<String>,
)