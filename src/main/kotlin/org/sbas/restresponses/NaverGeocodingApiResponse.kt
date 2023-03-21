package org.sbas.restresponses

data class NaverGeocodingApiResponse (
        var status: String,
        var errorMessage: String? = null,
        var meta: Meta? = null,
        var addresses: List<Address>? = null,

)

data class Meta(
        var totalCount: Int? = null,
        var page: Int? = null,
        var count: Int? = null,
)

data class Address(
        var roadAddress: String? = null,
        var jibunAddress: String? = null,
        var englishAddress: String? = null,
        var x: String? = null,
        var y: String? = null,
        var distance: Double? = null,
        var addressElements: List<AddressElements>? = null
)

data class AddressElements(
        var types: List<String>? = null,
        var longName: String? = null,
        var shortName: String? = null,
        var code: String? = null,
)