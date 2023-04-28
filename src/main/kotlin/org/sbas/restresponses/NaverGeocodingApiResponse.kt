package org.sbas.restresponses

import com.fasterxml.jackson.annotation.JsonProperty

data class NaverGeocodingApiResponse (
    @JsonProperty("status")
    var status: String,
    @JsonProperty("errorMessage")
    var errorMessage: String?,
    @JsonProperty("meta")
    var meta: Meta?,
    @JsonProperty("addressses")
    var addresses: List<Address>?
)
data class Meta(
    @JsonProperty("totalCount")
    var totalCount: Int?,
    @JsonProperty("page")
    var page: Int?,
    @JsonProperty("count")
    var count: Int?,
)

data class Address(
    @JsonProperty("roadAddress")
    var roadAddress: String?,
    @JsonProperty("jibunAddress")
    var jibunAddress: String?,
    @JsonProperty("englishAddress")
    var englishAddress: String?,
    @JsonProperty("x")//경도
    var x: String?,
    @JsonProperty("y")//위도
    var y: String?,
    @JsonProperty("distance")
    var distance: Double?,
    @JsonProperty("addressElements")
    var addressElements: List<AddressElements>?
)

data class AddressElements(
    @JsonProperty("types")
    var types: List<String>?,
    @JsonProperty("longName")
    var longName: String?,
    @JsonProperty("shortName")
    var shortName: String?,
    @JsonProperty("code")
    var code: String?,
)