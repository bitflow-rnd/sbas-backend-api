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

        fun getDstrCd1(addr: String): String {
            val map = mapOf(
                "서울" to "11",
                "부산" to "26",
                "대구" to "27",
                "인천" to "28",
                "광주" to "29",
                "대전" to "30",
                "울산" to "31",
                "세종" to "36",
                "경기" to "41",
                "강원" to "42",
                "충청북" to "43", "충북" to "43",
                "충청남" to "44", "충남" to "44",
                "전라북" to "45", "전북" to "45",
                "전라남" to "46", "전남" to "46",
                "경상북" to "47", "경북" to "47",
                "경상남" to "48", "경남" to "48",
            )

            return map.entries.find { addr.startsWith(it.key) }?.value ?: ""
        }

    }
}