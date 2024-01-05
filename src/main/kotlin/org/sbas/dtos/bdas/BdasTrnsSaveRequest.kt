package org.sbas.dtos.bdas

import org.sbas.entities.bdas.BdasTrns
import org.sbas.entities.bdas.BdasTrnsId
import org.sbas.utils.StringUtils
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.sbas.dtos.info.InfoCrewSaveReq

data class BdasTrnsSaveRequest(
    @field: NotBlank
    val ptId: String,
    @field: NotNull
    val bdasSeq: Int,
    @field: NotBlank
    val instId: String,
    @field: NotBlank
    val ambsNm: String,
    val crew1Id: String?,
    val crew1Pstn: String?,
    val crew1Nm: String?,
    val crew1Telno: String?,
    val crew2Id: String?,
    val crew2Pstn: String?,
    val crew2Nm: String?,
    val crew2Telno: String?,
    val crew3Id: String?,
    val crew3Pstn: String?,
    val crew3Nm: String?,
    val crew3Telno: String?,
    @field: NotBlank
    val chfTelno: String,
    val vecno: String?,
    val msg: String?,
) {
    fun toEntity(): BdasTrns {
        return BdasTrns(
            id = BdasTrnsId(ptId, bdasSeq),
            instId = instId,
            ambsNm = ambsNm,
            crew1Id = crew1Id,
            crew1Pstn = crew1Pstn,
            crew1Nm = crew1Nm,
            crew1Telno = crew1Telno,
            crew2Id = crew2Id,
            crew2Pstn = crew2Pstn,
            crew2Nm = crew2Nm,
            crew2Telno = crew2Telno,
            crew3Id = crew3Id,
            crew3Pstn = crew3Pstn,
            crew3Nm = crew3Nm,
            crew3Telno = crew3Telno,
            chfTelno = chfTelno,
            vecno = vecno,
            msg = msg,
            dprtDt = StringUtils.getYyyyMmDd(),
            dprtTm = StringUtils.getHhMmSs(),
        )
    }

    fun toInfoCrewSaveReqList(): List<InfoCrewSaveReq?> {
        val infoCrew1 = crew1Nm?.let {
            InfoCrewSaveReq(
                instId = instId,
                crewNm = crew1Nm,
                telno = crew1Telno,
                rmk = null,
                pstn = crew1Pstn
            )
        }
        val infoCrew2 = crew2Nm?.let {
            InfoCrewSaveReq(
                instId = instId,
                crewNm = crew2Nm,
                telno = crew2Telno,
                rmk = null,
                pstn = crew2Pstn,
            )
        }
        val infoCrew3 = crew3Nm?.let {
            InfoCrewSaveReq(
                instId = instId,
                crewNm = crew3Nm,
                telno = crew3Telno,
                rmk = null,
                pstn = crew3Pstn,
            )
        }
        return listOf(infoCrew1, infoCrew2, infoCrew3)
    }
}