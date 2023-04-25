package org.sbas.restparameters

import org.sbas.utils.NoArg

@NoArg
data class EgenApiListInfoParams(
    val q0: String, // 주소(시도)
    val q1: String, // 주소(시군구)
    val qz: String, // 기관구분
    val qd: String, // 진료과목
    val qt: String, // 진료요일
    val qn: String, // 기관명
    val ord: String, // 순서
    val pageNo: String = "1",
    val numOfRows: String = "10",
)

@NoArg
data class EgenApiLcInfoParams(
    val wgs84Lon: String, // 병원경도
    val wgs84Lat: String, // 병원위도
    val pageNo: String = "1",
    val numOfRows: String = "10",
)

@NoArg
data class EgenApiBassInfoParams(
    val hpId: String, // 기관 ID
    val pageNo: String = "1",
    val numOfRows: String = "10",
)
