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
    val hvec: Int?,
    val hv15: Int?,
    val hv16: Int?,
    val hv28: Int?,
    val hv30: Int?,
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
            ventilator = hvventiayn?.let { if (it.contains("N")) "N" else "Y" },
            ventilatorPreemie = hvventisoayn?.let { if (it.contains("N")) "N" else "Y" },
            incubator = hvincuayn?.let { if (it.contains("N")) "N" else "Y" },
            ecmo = hvecmoayn?.let { if (it.contains("N")) "N" else "Y" },
            highPressureOxygen = hvoxyayn?.let { if (it.contains("N")) "N" else "Y" },
            ct = hvctayn?.let { if (it.contains("N")) "N" else "Y" },
            mri = hvmriayn?.let { if (it.contains("N")) "N" else "Y" },
            bloodVesselImaging = hvangioayn?.let { if (it.contains("N")) "N" else "Y" },
            bodyTemperatureControl = hvhypoayn?.let { if (it.contains("N")) "N" else "Y" },
            emrgncyNrmlBed = hvec ?: 0,
            ngtvIsltnChild = hv15 ?: 0,
            nrmlIsltnChild = hv16 ?: 0,
            nrmlChildBed = hv28 ?: 0,
            emrgncyNrmlIsltnBed = hv30 ?: 0,
        )
    }
}