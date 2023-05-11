package org.sbas.dtos

import org.json.JSONArray
import org.json.JSONObject
import org.sbas.entities.info.InfoHosp
import org.sbas.utils.NoArg

@NoArg
data class InfoHospSaveReq(
    var hospId: String?,
    var hpid: String, // 기관 ID
    var dutyName: String?, // 기관명
    var dutyDivNam: String?, // 병원분류명
    var dutyTel1: String?, // 대표전화1
    var dutyAddr: String?, // 주소
    var postCdn1: String?, // 우편번호1
    var postCdn2: String?, // 우편번호2
    var wgs84Lon: String?, // 병원경도
    var wgs84Lat: String?, // 병원위도
    var dutyInf: String?, // 기관설명상세
    var dutyEtc: String?, // 비고

    var rnum: Int,
    var dutyDiv: String?,
    var dutyEmcls: String?,
    var dutyEmclsName: String?,
    var dutyEryn: String?,
    var dutyMapimg: String?,
    var dutyTel3: String?,
    var dutyTime1c: String?,
    var dutyTime2c: String?,
    var dutyTime3c: String?,
    var dutyTime4c: String?,
    var dutyTime5c: String?,
    var dutyTime6c: String?,
    var dutyTime7c: String?,
    var dutyTime8c: String?,
    var dutyTime1s: String?,
    var dutyTime2s: String?,
    var dutyTime3s: String?,
    var dutyTime4s: String?,
    var dutyTime5s: String?,
    var dutyTime6s: String?,
    var dutyTime7s: String?,
    var dutyTime8s: String?,
) {
    fun toEntity(siDo: String, siGunGu: String): InfoHosp {
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

    fun hospIdFormatter(number: Int): String {
        return "HP" + String.format("%08d", number)
    }
}