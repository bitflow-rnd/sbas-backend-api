package org.sbas.dtos

import kotlin.math.round

data class DsbdCardDetail(
  val title: String,
  val value: Long,
  val beforeValue: Long,
  val diff: Double = if (beforeValue != 0L) {
    round(((value - beforeValue) / beforeValue.toDouble()) * 100 * 1000) / 1000
  } else {
    Double.POSITIVE_INFINITY
  }
)
