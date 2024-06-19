package org.sbas.restdtos.response

import com.fasterxml.jackson.annotation.JsonProperty
import org.sbas.entities.svrt.SvrtColl
import org.sbas.entities.svrt.SvrtCollId

data class HisApiResponse(
  val elapsedTime: Int,
  val status: String,
  val errors: List<Any>,
  val message: String,
  val timestamp: String,
  val bodyType: String,
  val body: List<SvrtMntrInfo>,
)

// basedd-4 ~ basedd 데이터 조회
data class SvrtMntrInfo(
  @field: JsonProperty("adms_dt") val admsDt: String,
  @field: JsonProperty("dept_nm") val deptNm: String,
  @field: JsonProperty("msre_dt") val msreDt: String,
  @field: JsonProperty("msre_tm") val msreTm: String,
  @field: JsonProperty("room_nm") val roomNm: String,
  @field: JsonProperty("rslt_dt") val rsltDt: String,
  @field: JsonProperty("rslt_tm") val rsltTm: String,
  @field: JsonProperty("spcl_nm") val spclNm: String,
  @field: JsonProperty("ward_nm") val wardNm: String,
  val bdtp: String,
  val bun: String,
  val cre: String,
  val crp: String,
  val dbp: String,
  val hem: String,
  val hr: String,
  val ldh: String,
  val lym: String,
  val neu: String,
  val pid: String,
  val pla: String,
  val pot: String,
  val resp: String,
  val sbp: String,
  val sod: String,
  val spo2: String,
  val tag: String,
  val wbc: String,
) {
  fun toSvrtColl(ptId: String, hospId: String, rgstSeq: Int, collSeq: Int): SvrtColl {
    return SvrtColl(
      id = SvrtCollId(
        ptId = ptId,
        hospId = hospId,
        rgstSeq = rgstSeq,
        msreDt = msreDt,
        collSeq = collSeq,
      ),
      pid = pid,
      admsDt = admsDt,
      deptNm = deptNm,
      wardNm = wardNm,
      roomNm = roomNm,
      spclNm = spclNm,
      msreTm = msreTm,
      rsltDt = rsltDt,
      rsltTm = rsltTm,
      bun = bun,
      cre = cre,
      hem = hem,
      ldh = ldh,
      lym = lym,
      neu = neu,
      pla = pla,
      pot = pot,
      sod = sod,
      wbc = wbc,
      crp = crp,
      bdtp = bdtp,
      resp = resp,
      hr = hr,
      dbp = dbp,
      sbp = sbp,
      spo2 = spo2,
      oxygenApply = "",
    )
  }
}

