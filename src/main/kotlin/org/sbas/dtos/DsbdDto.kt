package org.sbas.dtos

import org.sbas.constants.enums.DsbdType
import org.sbas.entities.info.InfoDsbd
import kotlin.math.round

data class DsbdCardDetail(
  val type: DsbdType,
  val title: String,
  val value: Any,
  val beforeValue: Any,
  val diff: Double? = calculateDiff(value, beforeValue)
) {
  companion object {
    fun calculateDiff(value: Any, beforeValue: Any): Double? {
      if (value is Number && beforeValue is Number) {
        return round((value.toDouble() - beforeValue.toDouble()) * 100) / 100
      }
      return null
    }
  }

  fun toInfoDsbd(userId: String): InfoDsbd {
    return InfoDsbd(
      userId = userId,
      dsbdType = type,
      title = title,
      value = value.toString(),
      beforeValue = beforeValue.toString(),
    )
  }
}
