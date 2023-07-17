package org.sbas.utils

import java.text.SimpleDateFormat
import java.util.*

class StringUtils {

    companion object {

        private const val DATE_FORMAT = "yyyyMM"
        private const val DATE_FORMAT_DD = "yyyyMMdd"
        private const val TIME_FORMAT_HM = "HHmm"
        private const val TIME_FORMAT_HMS = "HHmmss"

        fun getYyyyMM(): String {
            val date = Date(System.currentTimeMillis())
            val simpleDateFormat = SimpleDateFormat(DATE_FORMAT)
            return simpleDateFormat.format(date)
        }

        fun getYyyyMmDd(): String {
            val date = Date(System.currentTimeMillis())
            val simpleDateFormat = SimpleDateFormat(DATE_FORMAT_DD)
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

        fun getKakaoSidoName(sido: String): String {
            val siDoMap = mapOf(
                "서울" to "서울",
                "부산" to "부산",
                "대구" to "대구",
                "인천" to "인천",
                "광주" to "광주",
                "대전" to "대전",
                "울산" to "울산",
                "세종" to "세종",
                "경기" to "경기",
                "강원" to "강원",
                "충청북" to "충북", "충북" to "충북",
                "충청남" to "충남", "충남" to "충남",
                "전라북" to "전북", "전북" to "전북",
                "전라남" to "전남", "전남" to "전남",
                "경상북" to "경북", "경북" to "경북",
                "경상남" to "경남", "경남" to "경남",
            )

            return siDoMap.entries.find { sido.startsWith(it.key) }?.value ?: ""
        }

        fun incrementCode(prefix: String, codeNumberLength: Int, code: String?): String {
            if (code.isNullOrEmpty()) {
                return prefix + 1.toString().padStart(codeNumberLength, '0')
            }

            val numberPart = code.substring(prefix.length).toInt()
            val incrementedNumber = numberPart + 1

            return prefix + incrementedNumber.toString().padStart(codeNumberLength, '0')
        }
    }
}