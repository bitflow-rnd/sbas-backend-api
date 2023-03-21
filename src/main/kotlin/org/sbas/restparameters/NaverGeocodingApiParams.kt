package org.sbas.restparameters

import javax.validation.constraints.NotNull

data class NaverGeocodingApiParams (

    @field: NotNull(message = "주소가 누락되었습니다.")
    var query: String,

    var coordinate: String?,

    var filter: String?,

    var language: String?,

    var page: Int?,

    var count: Int?

)