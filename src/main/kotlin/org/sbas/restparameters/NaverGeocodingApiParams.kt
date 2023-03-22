package org.sbas.restparameters

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

data class NaverGeocodingApiParams (

    @field: NotNull(message = "주소가 누락되었습니다.")
    @JsonProperty("query")
    var query: String,

    @JsonProperty("coordinate")
    var coordinate: String?,

    @JsonProperty("filter")
    var filter: String?,

    @JsonProperty("language")
    var language: String?,

    @JsonProperty("page")
    var page: Int?,

    @JsonProperty("count")
    var count: Int?

)