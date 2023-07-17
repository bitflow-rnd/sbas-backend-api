package org.sbas.restresponses

data class NaverOcrApiResponse(
    var version: String?,
    var requestId: String?,
    var timestamp: Long,
    var images: List<OcrImages>
){
    constructor() : this(null, null, 0, emptyList())
}

data class OcrImages(
    var uid: String?,
    var name: String?,
    var inferResult: String?,
    var message: String?,
    var matchedTemplate: MatchedTemplate?,
    var validationResult: OcrImageValidationResult?,
    var fields: List<OcrImageFields>?
){
    constructor() : this(null, null, null, null, null, null, emptyList())
}

data class MatchedTemplate(
    var id: String?,
    var name: String?,
) {
    constructor() : this(null, null)
}

data class OcrImageValidationResult(
    var result: String?
){
    constructor() : this(null)
}

data class OcrImageFields(
    var name: String?,
    var bounding: OcrImageFieldsBounding?,
    var valueType: String?,
    var inferText: String?,
    var inferConfidence: String?
){
    constructor() : this(null, null, null, null, null)
}

data class OcrImageFieldsBounding(
    var top: String?,
    var left: String?,
    var width: String?,
    var height: String?,
) {
    constructor() : this(null, null, null, null)
}

class FieldName {
    companion object {
        var nameList: MutableList<String> = mutableListOf(
            "수신보건소",
            "환자이름",
            "주민등록번호",
            "보호자명",
            "성별",
            "전화번호",
            "주소",
            "휴대전화번호",
            "질병명",
            "질병급",
            "직업",
            "코로나19 증상",
            "발병일",
            "진단일",
            "신고일",
            "확진검사 결과",
            "환자 등 분류",
            "입원여부",
            "사망여부",
            "신고구분",
            "비고",
            "요양기관명",
            "요양기관기호",
            "요양기관 전화번호",
            "요양기관주소",
            "진단의사 성명",
            "신고기관장 성명",
        )
    }
}

/*
{
    "version": "V1",
    "requestId": "string",
    "timestamp": 1689317162170,
    "images": [
        {
            "uid": "284e9c9444694b58a71ffe7130d40f17",
            "name": "epidreport",
            "inferResult": "SUCCESS",
            "message": "SUCCESS",
            "matchedTemplate": {
                "id": 24464,
                "name": "epidreport"
            },
            "validationResult": {
                "result": "NO_REQUESTED"
            },
            "fields": [
                {
                    "name": "수신보건소",
                    "bounding": {
                        "top": 14.0,
                        "left": 148.0,
                        "width": 136.0,
                        "height": 17.0
                    },
                    "valueType": "ALL",
                    "inferText": "대구광역시수성구보건소",
                    "inferConfidence": 0.9997
                },
 */