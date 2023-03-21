package org.sbas.restparameters

data class EgenApiListInfoParams(
    val q0: String? = null,
    val q1: String? = null,
    val qz: String? = null,
    val qd: String? = null,
    val qt: String? = null,
    val qn: String? = null,
    val ord: String? = null,
    val pageNo: String = "1",
    val numOfRows: String = "10",
)

data class EgenApiLcInfoParams(
    val wgs84Lon: String,
    val wgs84Lat: String,
    val pageNo: String = "1",
    val numOfRows: String = "10",
)

data class EgenApiBassInfoParams(
    val hpId: String? = null,
    val pageNo: String = "1",
    val numOfRows: String = "10",
)
