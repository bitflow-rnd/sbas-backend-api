package org.sbas.utils

import java.text.SimpleDateFormat
import java.util.*

class StringUtils {

    companion object {

        const val DATE_FORMAT = "yyyyMMdd"
        const val TIME_FORMAT_HM = "HHmm"
        const val TIME_FORMAT_HMS = "HHmmss"

        fun getYyyyMmDd(): String {
            val date = Date(System.currentTimeMillis())
            val simpleDateFormat = SimpleDateFormat(DATE_FORMAT)
            return simpleDateFormat.format(date)
        }

        fun getHhMm(): String {
            val date = Date(System.currentTimeMillis())
            val simpleDateFormat = SimpleDateFormat(TIME_FORMAT_HM)
            return simpleDateFormat.format(date)
        }

        fun getHhMmSs(): String {
            val date = Date(System.currentTimeMillis())
            val simpleDateFormat = SimpleDateFormat(TIME_FORMAT_HMS)
            return simpleDateFormat.format(date)
        }

    }
}