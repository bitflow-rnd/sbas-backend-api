package org.sbas.dtos.info

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.sbas.entities.info.InfoHosp
import org.sbas.utils.annotation.NoArg
import java.time.Instant
import javax.ws.rs.QueryParam

@NoArg
@JsonIgnoreProperties(ignoreUnknown = true)
data class InfoHospSaveReq(
    var hospId: String?,
    var hpid: String, // 기관 ID
    var dutyName: String?, // 기관명
    var dutyDivNam: String?, // 병원분류명
    var dutyTel1: String?, // 대표전화1
    var dutyTel3: String?, // 응급실전화
    var dutyAddr: String?, // 주소
    var postCdn1: String?, // 우편번호1
    var postCdn2: String?, // 우편번호2
    var wgs84Lon: String?, // 병원경도
    var wgs84Lat: String?, // 병원위도
    var dutyInf: String?, // 기관설명상세
    var dutyEtc: String?, // 비고
    var rnum: Int,
) {
    fun toEntity(siDo: String?, siGunGu: String?): InfoHosp {
        return InfoHosp(
            hospId = hospIdFormatter(this.rnum),
            hpId = this.hpid,
            dutyName = this.dutyName,
            dutyDivNam = this.dutyDivNam,
            dutyTel1 = this.dutyTel1,
            dutyAddr = this.dutyAddr,
            postCdn1 = this.postCdn1,
            postCdn2 = this.postCdn2,
            wgs84Lon = this.wgs84Lon,
            wgs84Lat = this.wgs84Lat,
            dutyInf = this.dutyInf,
            dutyEtc = this.dutyEtc,
            dstrCd1 = siDo,
            dstrCd2 = siGunGu
        )
    }

    fun toEntityFromScheduler(siDo: String?, siGunGu: String?): InfoHosp {
        return InfoHosp(
            hospId = hospIdFormatter(rnum),
            hpId = hpid,
            dutyName = dutyName,
            dutyDivNam = dutyDivNam,
            dutyTel1 = dutyTel1,
            dutyAddr = dutyAddr,
            postCdn1 = postCdn1,
            postCdn2 = postCdn2,
            wgs84Lon = wgs84Lon,
            wgs84Lat = wgs84Lat,
            dutyInf = dutyInf,
            dutyEtc = dutyEtc,
            dstrCd1 = siDo,
            dstrCd2 = siGunGu,
        )
    }

    private fun hospIdFormatter(number: Int): String {
        return "HP" + String.format("%08d", number)
    }
}

data class InfoHospDetailDto(
    var infoHosp: InfoHosp,
    var medicCnt: Long,
)

@NoArg
data class InfoHospSearchParam(
    @field: QueryParam("hospId") var hospId: String?,
    @field: QueryParam("dutyName") var dutyName: String?,
    @field: QueryParam("dstrCd1") var dstrCd1: String?,
    @field: QueryParam("dstrCd2") var dstrCd2: String?,
    @field: QueryParam("page") var page: Int? = 0,
    @field: QueryParam("dutyDivNams") var dutyDivNams: String?,
) {
    val pageSize: Int = 10
    var dutyDivNam: MutableList<String>? = mutableListOf()
}

data class InfoHospListDto(
    val hospId: String?,
    val hpId: String?,
    val dutyName: String?,
    val dutyDivNam: String?,
    val dstrCd1: String?,
    val dstrCd2: String?,
    val dutyTel1: String?,
    val dutyTel3: String?,
    val updtDttm: Instant?,
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

data class InfoHospWithUser(
    val hospId: String,
    val dutyName: String,
    val instId: String,
    val instNm: String,
    val userId: String,
)

data class InfoHospId(
    val hospId: String,
    val hpId: String,
)

data class AvalHospDto(
    val hospId: String,
    val dutyName: String?,
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