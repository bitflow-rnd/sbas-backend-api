package org.sbas.restresponses

data class NaverOcrApiResponse(
    var version: String,
    var requestId: String,
    var timestamp: Long,
    var images: List<OcrImages>
)

data class OcrImages(
    var uid: String,
    var name: String,
    var inferResult: String,
    var message: String,
    var validationResult: OcrImageValidationResult,
    var fields: List<OcrImageFields>
)

data class OcrImageValidationResult(
    var result: String
)

data class OcrImageFields(
    var valueType: String,
    var boundingPoly: List<OcrImageFieldsBoundingPoly>,
    var inferText: String,
    var inferConfidence: String
)

data class OcrImageFieldsBoundingPoly(
    var vertices: List<OcrImageFieldsBoundingPolyXy>
)

data class OcrImageFieldsBoundingPolyXy(
    var x: Float,
    var y: Float
)

/*
{
    "version": "V1",
    "requestId": "string",
    "timestamp": 1679385743266,
    "images": [
        {
            "uid": "db1d9e43f38a4199b97daf835b4293ca",
            "name": "medium",
            "inferResult": "SUCCESS",
            "message": "SUCCESS",
            "validationResult": {
                "result": "NO_REQUESTED"
            },
            "fields": [
                {
                    "valueType": "ALL",
                    "boundingPoly": {
                        "vertices": [
                            {
                                "x": 40.0,
                                "y": 14.0
                            },
                            {
                                "x": 110.0,
                                "y": 14.0
                            },
                            {
                                "x": 110.0,
                                "y": 30.0
                            },
                            {
                                "x": 40.0,
                                "y": 30.0
                            }
                        ]
                    },
                    "inferText": "수신보건소",
                    "inferConfidence": 0.9994
                },

            ]
        }
    ]
}
 */