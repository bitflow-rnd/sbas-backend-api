package org.sbas.parameters

data class NewsScoreParameters(
    var breath: Int,  // 분당호흡수
    var spo2: Double,    // 산소포화도
    var o2Apply: String, // 산소투여여부
    var sbp: Int,     // 수축기혈압
    var pulse: Int,   // 맥박
    var avpu: String,    // 의식상태
    var bdTemp: Double,  // 체온
)