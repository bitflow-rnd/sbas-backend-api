package org.sbas.dtos.info

import com.fasterxml.jackson.annotation.JsonProperty
import org.sbas.utils.annotation.NoArg

@NoArg
data class InfoHospResponse(
    var hpid: String?,
    var dutyName: String?,
    var dutyAddr: String?,
    var dutyTel1: String?, // 대표전화1
    var dutyTel3: String?, //    응급실전화
    var hvec: String?,   // 응급실
    var hvoc: String?,   // 수술실
    var hvcc: String?,   // 신경중환자
    var hvncc: String?,   // 신생중환자
    var hvccc: String?,    // 흉부중환자
    var hvicc: String?,   // 일반중환자
    var hvgc: String?,  // 입원실
    var dutyHayn: String?,  //  입원실가용여부
    var dutyHano: String?, //   병상수
    var dutyInf: String?, //   기관설명상세
    var dutyMapimg: String?, //   간이약도
    var dutyEryn: String?, //    응급실운영여부(1/2)

    var dutyTime1c: String?, // 진료시간(월요일)C
    var dutyTime2c: String?, // 진료시간(화요일)C
    var dutyTime3c: String?, // 진료시간(수요일)C
    var dutyTime4c: String?, // 진료시간(목요일)C
    var dutyTime5c: String?, // 진료시간(금요일)C
    var dutyTime6c: String?, // 진료시간(토요일)C
    var dutyTime7c: String?, // 진료시간(일요일)C
    var dutyTime8c: String?, // 진료시간(공휴일)C
    var dutyTime1s: String?, // 진료시간(월요일)S
    var dutyTime2s: String?, // 진료시간(화요일)S
    var dutyTime3s: String?, // 진료시간(수요일)S
    var dutyTime4s: String?, // 진료시간(목요일)S
    var dutyTime5s: String?, // 진료시간(금요일)S
    var dutyTime6s: String?, // 진료시간(토요일)S
    var dutyTime7s: String?, // 진료시간(일요일)S
    var dutyTime8s: String?, // 진료시간(공휴일)S

    @JsonProperty("MKioskTy25")
    var mkioskTy25: String?, // 응급실(Emergency gatekeeper)
    @JsonProperty("MKioskTy1")
    var mkioskTy1: String?, // 뇌출혈수술
    @JsonProperty("MKioskTy2")
    var mkioskTy2: String?, // 뇌경색의재관류
    @JsonProperty("MKioskTy3")
    var mkioskTy3: String?, // 심근경색의재관류
    @JsonProperty("MKioskTy4")
    var mkioskTy4: String?, // 복부손상의수술
    @JsonProperty("MKioskTy5")
    var mkioskTy5: String?,  // 사지접합의수술
    @JsonProperty("MKioskTy6")
    var mkioskTy6: String?,  // 응급내시경
    @JsonProperty("MKioskTy7")
    var mkioskTy7: String?,  // 응급투석
    @JsonProperty("MKioskTy8")
    var mkioskTy8: String?,  // 조산산모
    @JsonProperty("MKioskTy9")
    var mkioskTy9: String?,  // 정신질환자
    @JsonProperty("MKioskTy10")
    var mkioskTy10: String?, // 신생아
    @JsonProperty("MKioskTy11")
    var mkioskTy11: String?, // 중증화상

    var o001: String?, // 응급실 일반병상
    var o002: String?, // 응급실 소아 병상
    var o003: String?, // 응급실 음압 격리 병상
    var o004: String?, // 응급실 일반 격리 병상
    var o005: String?, // 응급전용 중환자실
    var o006: String?, // 내과중환자실
    var o007: String?, // 외과중환자실
    var o008: String?, // 신생아중환자실
    var o009: String?, // 소아 중환자실
    var o010: String?, // 소아응급전용
    var o011: String?, // 신경과중환자실
    var o012: String?, // 신경외과중환자실
    var o013: String?, // 화상중환자실
    var o014: String?, // 외상중환자실
    var o015: String?, // 심장내과 중환자실
    var o016: String?, // 흉부외과 중환자실
    var o017: String?, // 일반 중환자실
    var o018: String?, // 중환자실 내 음압 격리 병상
    var o019: String?, // 응급전용 입원실
    var o020: String?, // 소아응급전용
    var o021: String?, // 외상전용 입원실
    var o022: String?, // 수술실
    var o023: String?, // 외상전용 수술실
    var o024: String?, // 정신과 폐쇄 병상
    var o025: String?, // 음압 격리 병상
    var o026: String?, // 분만실
    var o027: String?, // CT
    var o028: String?, // MRI
    var o029: String?, // 혈관촬영기
    var o030: String?, // 인공호흡기
    var o031: String?, // 인공호흡기(소아)
    var o032: String?, // 인큐베이터
    var o033: String?, // CRRT
    var o034: String?, // ECMO
    var o035: String?, // 치료적 저체온 요법
    var o036: String?, // 화상전용 처치실
    var o037: String?, // 고압산소치료기
    var o038: String?, // 일반입원실

    var wgs84Lon: String?, //    병원경도
    var wgs84Lat: String?, //   병원위도
    var dgidIdName: String?, //   진료과목

    var hpbdn: String?, //   병상수
    var hpccuyn: String?, //   흉부중환자실
    var hpcuyn: String?, //   신경중환자실
    var hperyn: String?, //   응급실
    var hpgryn: String?, //   입원실
    var hpicuyn: String?, //   일반중환자실
    var hpnicuyn: String?, //   신생아중환자실
    var hpopyn: String?, //  수술실
    var dgidIdNameList: List<String>?,

    var dutyDiv: String?,
    var dutyDivNam: String?,
    var dutyEmcls: String?,
    var dutyEmclsName: String?,
    var dutyFax: String?,
    var startTime: Int?,
    var endTime: Int?,
) {
    init {
        mkioskTy25 = mkioskTy25?.trim()
        mkioskTy1 = mkioskTy1?.trim()
        mkioskTy2 = mkioskTy2?.trim()
        mkioskTy3 = mkioskTy3?.trim()
        mkioskTy4 = mkioskTy4?.trim()
        mkioskTy5 = mkioskTy5?.trim()
        mkioskTy6 = mkioskTy6?.trim()
        mkioskTy7 = mkioskTy7?.trim()
        mkioskTy8 = mkioskTy8?.trim()
        mkioskTy9 = mkioskTy9?.trim()
        mkioskTy10 = mkioskTy10?.trim()
        mkioskTy11 = mkioskTy11?.trim()
        dgidIdNameList = dgidIdName?.split(",")
    }
}
