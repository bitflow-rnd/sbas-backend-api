package org.sbas.entities.svrt

import jakarta.persistence.*
import org.sbas.entities.CommonEntity
import java.io.Serial
import java.io.Serializable

/**
 * 중증 관찰 정보
 */
@Entity
@Table(name = "svrt_coll_sample")
class SvrtCollSample(
  @Id
  @Column(name = "pid", nullable = false, length = 10)
  var pid: String, // 병원 PID

  @Column(name = "hosp_id", nullable = false, length = 10)
  var hospId: String, // 병원 ID

  @Column(name = "adms_dt", nullable = false, length = 8)
  var admsDt: String, // 입원 날짜

  @Column(name = "dept_nm", length = 20)
  var deptNm: String? = null, // 진료과 이름

  @Column(name = "ward_nm", length = 20)
  var wardNm: String? = null, // 병동 이름

  @Column(name = "room_nm", length = 20)
  var roomNm: String? = null, // 병실 이름

  @Column(name = "spcl_nm", length = 10)
  var spclNm: String? = null, // 담당의 이름

  @Id
  @Column(name = "msre_dt", nullable = false, length = 8)
  var msreDt: String, // 측정 날짜

  @Column(name = "msre_tm", nullable = false, length = 6)
  var msreTm: String, // 측정 시간

  @Column(name = "rslt_dt", nullable = false, length = 8)
  var rsltDt: String, // 결과 날짜

  @Column(name = "rslt_tm", nullable = false, length = 6)
  var rsltTm: String, // 결과 시간

  @Column(name = "bun", length = 10)
  var bun: String? = null, // BUN

  @Column(name = "cre", length = 10)
  var cre: String? = null, // Creatinine

  @Column(name = "hem", length = 10)
  var hem: String? = null, // Hemoglobin

  @Column(name = "ldh", length = 10)
  var ldh: String? = null, // LDH

  @Column(name = "lym", length = 10)
  var lym: String? = null, // Lymphocytes

  @Column(name = "neu", length = 10)
  var neu: String? = null, // Neutrophils

  @Column(name = "pla", length = 10)
  var pla: String? = null, // Platelet Count

  @Column(name = "pot", length = 10)
  var pot: String? = null, // Potassium

  @Column(name = "sod", length = 10)
  var sod: String? = null, // Sodium

  @Column(name = "wbc", length = 10)
  var wbc: String? = null, // WBC Count

  @Column(name = "crp", length = 10)
  var crp: String? = null, // hs-CRP

  @Column(name = "bdtp", length = 10)
  var bdtp: String? = null, // 체온

  @Column(name = "resp", length = 10)
  var resp: String? = null, // 호흡수

  @Column(name = "hr", length = 10)
  var hr: String? = null, // 맥박

  @Column(name = "dbp", length = 10)
  var dbp: String? = null, // 이완기 혈압

  @Column(name = "sbp", length = 10)
  var sbp: String? = null, // 수축기 혈압

  @Column(name = "spo2", length = 10)
  var spo2: String? = null, // 산소포화도

  @Column(name = "oxygen", length = 12)
  var oxygenApply: String? = null, // 산소치료수준
) : CommonEntity() {
  fun toSvrtColl(ptId: String, rgstSeq: Int, collSeq: Int): SvrtColl {
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
      oxygenApply = oxygenApply
    )
  }
}