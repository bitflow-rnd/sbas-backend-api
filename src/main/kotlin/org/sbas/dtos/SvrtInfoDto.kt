package org.sbas.dtos

data class SvrtInfoRsps(
  val ptId: String,
  val hospId: String,
  val anlyDt: String,
  val msreDt: String,
  val prdtDt: String,
  val covSf: String,
  val oxygenApply: String,
)