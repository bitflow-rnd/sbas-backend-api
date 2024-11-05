package org.sbas.dtos.info

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.ws.rs.QueryParam
import org.jboss.resteasy.reactive.Separator
import org.sbas.entities.info.InfoHosp
import org.sbas.utils.annotation.NoArg
import java.time.Instant

@NoArg
@JsonIgnoreProperties(ignoreUnknown = true)
data class InfoHospSaveReq(
    var hospId: String?,
    var hpid: String, // 기관 ID
    var dutyName: String?, // 기관명
    var dutyEmclsName: String?, // 병원분류명
    var dutyTel1: String?, // 대표전화1
    var dutyTel3: String?, // 응급실전화
    var dutyAddr: String?, // 주소
    var wgs84Lon: String?, // 병원경도
    var wgs84Lat: String?, // 병원위도
    var rnum: Int,
) {
    fun toEntity(siDo: String?, siGunGu: String?): InfoHosp {
        return InfoHosp(
            hospId = hospIdFormatter(this.rnum),
            hpId = this.hpid,
            dutyName = this.dutyName,
            dutyDivNam = this.dutyEmclsName,
            dutyTel1 = this.dutyTel1,
            dutyTel3 = this.dutyTel3,
            dutyAddr = this.dutyAddr,
            wgs84Lon = this.wgs84Lon,
            wgs84Lat = this.wgs84Lat,
            dstr1Cd = siDo,
            dstr2Cd = siGunGu
        )
    }

    private fun hospIdFormatter(number: Int): String {
        return "HP" + String.format("%08d", number)
    }
}

@NoArg
data class InfoHospSearchParam(
    @field: QueryParam("hospId") var hospId: String?,
    @field: QueryParam("dutyName") var dutyName: String?,
    @field: QueryParam("dstr1Cd") var dstr1Cd: String?,
    @field: QueryParam("dstr2Cd") var dstr2Cd: String?,
    @field: QueryParam("page") var page: Int? = 0,
    @field: QueryParam("dutyDivNam") var dutyDivNam: String? = null,
    @field: [QueryParam("svrtTypeCd") Separator(",")] var svrtTypeCd: List<String>?,
    @field: [QueryParam("reqBedTypeCd") Separator(",")] var reqBedTypeCd: List<String>?,
    @field: [QueryParam("equipment") Separator(",")] var equipment: List<String>?,
)

data class InfoHospListDto(
    val hospId: String?,
    val hpId: String?,
    val dutyName: String?,
    val dutyDivNam: String?,
    val dstr1Cd: String?,
    val dstr2Cd: String?,
    val dutyTel1: String?,
    val dutyTel3: String?,
    val updtDttm: Instant?,
    val gnbdIcu: Int, // hv22 54
    val npidIcu: Int, // hv23 55
    val gnbdSvrt: Int, // hv24 56
    val gnbdSmsv: Int, // hv25
    val gnbdModr: Int, // hv26
    val ventilator: String?,
    val ventilatorPreemie: String?,
    val incubator: String?,
    val ecmo: String?,
    val highPressureOxygen: String?,
    val ct: String?,
    val mri: String?,
    val bloodVesselImaging: String?,
    val bodyTemperatureControl: String?,
    val emrgncyNrmlBed: Int?, // hvec - 일반(응급실일반병상)
    val ngtvIsltnChild: Int?, // hv15 - 소아 음압격리
    val nrmlIsltnChild: Int?, // hv16 - 소아 일반격리
    val nrmlChildBed: Int?, // hv28 - 소아
    val emrgncyNgtvIsltnBed: Int?, // hv29 - 응급실 음압 격리 병상
    val emrgncyNrmlIsltnBed: Int?, // hv30 - 응급실 일반 격리 병상
    val isltnMedAreaNgtvIsltnBed: Int?, // hv13 - 격리 진료 구역 음압 격리 병상
    val isltnMedAreaNrmlIsltnBed: Int?, // hv14 - 격리 진료 구역 일반 격리 병상
    val cohtBed: Int?,

    var medicalStaffCount: Long?,
) {
    init {
        medicalStaffCount = medicalStaffCount ?: 0
    }
}

data class InfoHospWithUser(
  val hospId: String,
  val dutyName: String,
  val userId: String,
)

data class InfoHospId(
    val hospId: String,
    val hpId: String,
    val dstr1Cd: String?,
    val dstr1CdNm: String?,
    val attcId: String?,
)

data class AvalHospDto(
    val hospId: String,
    val dutyName: String?,
    val dutyDivNam: String?,
    val wgs84Lon: String?,
    val wgs84Lat: String?,
    val dutyAddr: String?,
    val gnbdIcu: Int, // hv22 54
    val npidIcu: Int, // hv23 55
    val gnbdSvrt: Int, // hv24 56
    val gnbdSmsv: Int, // hv25
    val gnbdModr: Int, // hv26
    var ventilator: String?,
    var ventilatorPreemie: String?,
    var incubator: String?,
    var ecmo: String?,
    var highPressureOxygen: String?,
    var ct: String?,
    var mri: String?,
    var bloodVesselImaging: String?,
    var bodyTemperatureControl: String?,
)