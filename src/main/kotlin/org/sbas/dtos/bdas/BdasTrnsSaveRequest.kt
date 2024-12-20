package org.sbas.dtos.bdas

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.sbas.dtos.info.InfoCrewSaveReq
import org.sbas.entities.bdas.BdasTrns
import org.sbas.entities.bdas.BdasTrnsId
import org.sbas.entities.info.InfoCrew
import org.sbas.entities.info.InfoCrewId
import org.sbas.utils.StringUtils

data class BdasTrnsSaveRequest(
  @field: NotBlank
  val ptId: String,
  @field: NotNull
  val bdasSeq: Int,
  @field: NotBlank
  val instId: String,
  @field: NotBlank
  val ambsNm: String,
  val crew1Id: Int?,
  val crew1Pstn: String?,
  val crew1Nm: String?,
  val crew1Telno: String?,
  val crew2Id: Int?,
  val crew2Pstn: String?,
  val crew2Nm: String?,
  val crew2Telno: String?,
  val crew3Id: Int?,
  val crew3Pstn: String?,
  val crew3Nm: String?,
  val crew3Telno: String?,
  @field: NotBlank
  val chfTelno: String,
  val vecno: String?,
  val msg: String?,
) {
  fun toEntity(saveInfoCrewList: List<InfoCrew>): BdasTrns {
    return BdasTrns(
      id = BdasTrnsId(ptId, bdasSeq),
      instId = instId,
      ambsNm = ambsNm,
      crew1Id = saveInfoCrewList.getOrNull(0)?.id?.crewId?.toString(),
      crew1Pstn = crew1Pstn,
      crew1Nm = crew1Nm,
      crew1Telno = crew1Telno,
      crew2Id = saveInfoCrewList.getOrNull(1)?.id?.crewId?.toString(),
      crew2Pstn = crew2Pstn,
      crew2Nm = crew2Nm,
      crew2Telno = crew2Telno,
      crew3Id = saveInfoCrewList.getOrNull(2)?.id?.crewId?.toString(),
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

  fun toInfoCrewList(): List<InfoCrew?> {
    val infoCrew1 = if (crew1Id != null && crew1Id != 0) {
      InfoCrew(
        id = InfoCrewId(instId, crew1Id),
        crewNm = crew1Nm!!,
        telno = crew1Telno,
        pstn = crew1Pstn,
        rmk = null,
      )
    } else null
    val infoCrew2 = if (crew2Id != null && crew2Id != 0) {
      InfoCrew(
        id = InfoCrewId(instId, crew2Id),
        crewNm = crew2Nm!!,
        telno = crew2Telno,
        pstn = crew2Pstn,
        rmk = null,
      )
    } else null
    val infoCrew3 = if (crew3Id != null && crew3Id != 0) {
      InfoCrew(
        id = InfoCrewId(instId, crew3Id),
        crewNm = crew3Nm!!,
        telno = crew3Telno,
        pstn = crew3Pstn,
        rmk = null,
      )
    } else null
    return listOf(infoCrew1, infoCrew2, infoCrew3)
  }

  fun toInfoCrewSaveReqList(): List<InfoCrewSaveReq?> {
    val infoCrew1 = if (!crew1Nm.isNullOrBlank()) {
      InfoCrewSaveReq(
        instId = instId,
        crewNm = crew1Nm,
        telno = crew1Telno,
        rmk = null,
        pstn = crew1Pstn
      )
    } else null

    val infoCrew2 = if (!crew2Nm.isNullOrBlank()) {
      InfoCrewSaveReq(
        instId = instId,
        crewNm = crew2Nm,
        telno = crew2Telno,
        rmk = null,
        pstn = crew2Pstn,
      )
    } else null
    val infoCrew3 = if (!crew3Nm.isNullOrBlank()) {
      InfoCrewSaveReq(
        instId = instId,
        crewNm = crew3Nm,
        telno = crew3Telno,
        rmk = null,
        pstn = crew3Pstn,
      )
    } else null
    return listOf(infoCrew1, infoCrew2, infoCrew3)
  }
}