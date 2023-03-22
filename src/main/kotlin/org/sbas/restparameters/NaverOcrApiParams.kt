package org.sbas.restparameters

import javax.validation.constraints.NotNull

data class NaverOcrApiParams(

        @field: NotNull (message = "이미지 정보가 누락되었습니다")
        var images: List<OcrApiImagesParam>,

        // 버전 정보 필수로 V1, 혹은 V2를 입력, V2 사용을 권장하며, V2사용시 boundingPoly 정보가 제공됨
        @field: NotNull (message = "버전 정보가 누락되었습니다")
        var version: String,

        @field: NotNull (message = "API 호출 UUID가 누락되었습니다")
        var requestId: String,

        @field: NotNull (message = "API 호출 Timestamp가 누락되었습니다")
        var timestamp: Long,
        // OCR 인식시 요청할 언어 정보
        var lang: String?

)

data class OcrApiImagesParam(
    @field: NotNull (message = "이미지 포맷이 누락되었습니다")
    var format: String,
    @field: NotNull (message = "이미지 고유명이 누락되었습니다")
    var name: String,
    // data, url 둘 중 하나는 필수입력
    // data는 Base64 인코드 된 이미지
    var data: String?,
    // url은 이미지 인터넷 주소
    var url: String?
)
