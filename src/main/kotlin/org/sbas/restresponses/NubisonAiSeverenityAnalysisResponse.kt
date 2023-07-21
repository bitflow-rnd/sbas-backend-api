package org.sbas.restresponses

import com.fasterxml.jackson.annotation.JsonProperty

data class NubisonAiSeverenityAnalysisResponse(

    @JsonProperty("model_name")
    var modelName: String,

    @JsonProperty("model_version")
    var modelVersion: String,

    @JsonProperty("id")
    var id: String,

    @JsonProperty("parameters")
    var parameters: String?,

    @JsonProperty("outputs")
    var outputs: List<OutputData>
)

data class OutputData(

    @JsonProperty("name")
    var name: String,

    @JsonProperty("shape")
    var shape: List<Int>,

    @JsonProperty("datatype")
    var dataType: String,

    @JsonProperty("parameters")
    var parameters: String?,

    @JsonProperty("data")
    var data: List<Float>
)