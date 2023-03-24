package org.sbas.restparameters

import org.sbas.utils.NoArg

@NoArg
data class EgenApiListInfoParams(
    val q0: String,
    val q1: String,
    val qz: String,
    val qd: String,
    val qt: String,
    val qn: String,
    val ord: String,
    val pageNo: String = "1",
    val numOfRows: String = "10",
)

@NoArg
data class EgenApiLcInfoParams(
    val wgs84Lon: String,
    val wgs84Lat: String,
    val pageNo: String = "1",
    val numOfRows: String = "10",
)

@NoArg
data class EgenApiBassInfoParams(
    val hpId: String,
    val pageNo: String = "1",
    val numOfRows: String = "10",
)
