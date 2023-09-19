package org.sbas.dtos.info

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.sbas.entities.info.InfoBed
import org.sbas.utils.annotation.NoArg

@NoArg
@JsonIgnoreProperties(ignoreUnknown = true)
data class InfoBedSaveReq(
    val hpid: String,
    var hv22: Int? = null, // hv22
    var hv23: Int? = null, // hv23
    var hv24: Int? = null, // hv24
    var hv25: Int? = null, // hv25
    var hv26: Int? = null, // hv26
    var hv27: Int? = null, // hv27
    var hvventiayn: String? = null,
    var hvventisoayn: String? = null,
    var hvincuayn: String? = null,
    var hvecmoayn: String? = null,
    var hvoxyayn: String? = null,
    var hvctayn: String? = null,
    var hvmriayn: String? = null,
    var hvangioayn: String? = null,
    var hvhypoayn: String? = null,
) {
    fun toEntity(hospId: String): InfoBed {
        return InfoBed(
            hospId = hospId,
            hpId = hpid,
            gnbdIcu = hv22 ?: 0,
            npidIcu = hv23 ?: 0,
            gnbdSvrt = hv24 ?: 0,
            gnbdSmsv = hv25 ?: 0,
            gnbdModr = hv26 ?: 0,
            cohtBed = hv27 ?: 0,
            npibSvrt = null,
            npibSmsv = null,
            npibModr = null,
            npibLbrInft = null,
            ventilator = hvventiayn,
            ventilatorPreemie = hvventisoayn,
            incubator = hvincuayn,
            ecmo = hvecmoayn,
            highPressureOxygen = hvoxyayn,
            ct = hvctayn,
            mri = hvmriayn,
            bloodVesselImaging = hvangioayn,
            bodyTemperatureControl = hvhypoayn,
        )
    }
}