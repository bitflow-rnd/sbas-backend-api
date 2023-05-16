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

data class NaverReverseGeocodingApiResponse (
    @JsonProperty("name")
    var name: String?,
    @JsonProperty("code")
    var code: Code?,
    @JsonProperty("region")
    var region: RegionElement?,
    @JsonProperty("land")
    var land: Land?,
    @JsonProperty("addition0")
    var addition0: Addition?,
    @JsonProperty("addition1")
    var addition1: Addition?,
    @JsonProperty("addition2")
    var addition2: Addition?,
    @JsonProperty("addition3")
    var addition3: Addition?,
    @JsonProperty("addition4")
    var addition4: Addition?,
)

data class Code(
    @JsonProperty("id")
    var id: String?,
    @JsonProperty("type")
    var type: String?,
    @JsonProperty("mappingId")
    var mappingId: String?,
)

data class RegionElement(
    @JsonProperty("area0")
    var area0: Area?,
    @JsonProperty("area1")
    var area1: Area?,
    @JsonProperty("area2")
    var area2: Area?,
    @JsonProperty("area3")
    var area3: Area?,
    @JsonProperty("area4")
    var area4: Area?
)

data class Area(
    @JsonProperty("name")
    var name: String,
    @JsonProperty("coords")
    var cords: Coords?,
)

data class Coords(
    @JsonProperty("center")
    var center: Center?
)

data class Center(
    @JsonProperty("crs")
    var crs: String?,
    @JsonProperty("x")
    var x: Float?,
    @JsonProperty("y")
    var y: Float?,
)

data class Land(
    @JsonProperty("type")
    var type: String?,
    @JsonProperty("name")
    var name: String?,
    @JsonProperty("number1")
    var number1: String?,
    @JsonProperty("number2")
    var number2: String?,
    @JsonProperty("coords")
    var coords: Coords
)

data class Addition(
    @JsonProperty("type")
    var type: String?,
    @JsonProperty("value")
    var value: String?,
)