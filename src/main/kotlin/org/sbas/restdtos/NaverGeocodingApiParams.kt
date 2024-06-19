package org.sbas.restdtos

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class NaverGeocodingApiParams (

    @field: NotNull(message = "주소가 누락되었습니다.")
    @JsonProperty("query")
    var query: String,

    @JsonProperty("coordinate")
    var coordinate: String? = null,

    @JsonProperty("filter")
    var filter: String? = null,

    @JsonProperty("language")
    var language: String? = null,

    @JsonProperty("page")
    var page: Int? = null,

    @JsonProperty("count")
    var count: Int? = null,

)