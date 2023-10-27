package org.sbas.dtos.info

import com.fasterxml.jackson.annotation.JsonProperty
import org.sbas.constants.enums.*
import org.sbas.utils.annotation.NoArg
import java.time.Instant

data class HospInfoRes(
    val hospBasicInfo: HospBasicInfo,
    val hospDetailInfo: HospDetailInfo,
    val hospMedInfo: List<HospMedInfo>,
    val hospMedInfoCount: Int,
)

@NoArg
data class HospBasicInfo(
    var hpid: String?,
    var attcId: String?,
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
    var startTime: String?,
    var endTime: String?,
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

        dutyTime1c = dutyTime1c?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        dutyTime2c = dutyTime2c?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        dutyTime3c = dutyTime3c?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        dutyTime4c = dutyTime4c?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        dutyTime5c = dutyTime5c?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        dutyTime6c = dutyTime6c?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        dutyTime7c = dutyTime7c?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        dutyTime8c = dutyTime8c?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        dutyTime1s = dutyTime1s?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        dutyTime2s = dutyTime2s?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        dutyTime3s = dutyTime3s?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        dutyTime4s = dutyTime4s?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        dutyTime5s = dutyTime5s?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        dutyTime6s = dutyTime6s?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        dutyTime7s = dutyTime7s?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        dutyTime8s = dutyTime8s?.replaceFirst(Regex("(\\d{2})(\\d{2})"), "$1:$2")
    }
}

data class HospDetailInfo(
    val dutytel3: String?, // 응급실전화
    val hvdnm: String?, // 당직의
    val hv1: String?, // 당직의 직통연락처
    val hvamyn: String?, // 구급차
    val hvctayn: String?, // CT
    val hvmriayn: String?, // MRI
    val hvangioayn: String?, // 조영촬영기
    val hvventiayn: String?, // 인공호흡기가용
    val hv5: String?, // 신경과입원실
    val hv7: String?, // 약물중환자
    val hv10: String?, // VENTI(소아)
    val hv11: String?, // 인큐베이터(보육기)
    val hv42: String?, // 분만실

    val hvec: String?, // 응급실
    val hvoc: String?, // 수술실
    val hvcc: String?, // 신경중환자
    val hvncc: String?, // 신생중환자
    val hvccc: String?, // 흉부중환자
    val hvicc: String?, // 일반중환자
    val hvgc: String?, // 입원실
    val hv2: String?, // 내과중환자실
    val hv3: String?, // 외과중환자실
    val hv4: String?, // 외과입원실
    val hv6: String?, // 신경외과중환자실
    val hv8: String?, // 화상중환자
    val hv9: String?, // 외상중환자

    val hv13: String?, // 격리진료구역 음압격리 병상
    val hv14: String?, // 격리진료구역 일반격리 병상
    val hv15: String?, // 소아 음압격리병상
    val hv16: String?, // 소아 일반격리병상
    val hv17: String?, // 응급전용 중환자실 음압 격리병상
    val hv18: String?, // 응급전용 중환자실 일반 격리병상
    val hv19: String?, // 응급전용 입원실 음압 격리병상
    val hv21: String?, // 응급전용 입원실 일반격리 병상
    val hv22: String?, // 감염병 전담병상 중환자실
    val hv23: String?, // 감염병 전담병상 중환자실 내 음압격리병상
    val hv24: String?, // 감염병 전담병상 중증 일반병상
    val hv25: String?, // 감염병 전담병상 준-중증 일반병상
    val hv26: String?, // 감염병 전담병상 중등증 일반병상
    val hv27: String?, // 코호트 격리구역 병상
    val hv28: String?, // 응급실 소아 병상
    val hv29: String?, // 응급실 음압 격리 병상
    val hv30: String?, // 응급실 일반 격리 병상
    val hv31: String?, // 응급전용 중환자실
    val hv32: String?, // 소아 중환자실
    val hv33: String?, // 소아응급전용 중환자실 병상
    val hv34: String?, // 심장내과 중환자실
    val hv35: String?, // 중환자실 내 음압 격리 병상
    val hv36: String?, // 응급전용 입원실
    val hv37: String?, // 소아응급전용 입원 병상
    val hv38: String?, // 외상전용 입원실
    val hv39: String?, // 외상전용 수술실
    val hv40: String?, // 정신과 폐쇄 병상
    val hv41: String?, // 음압 격리 병상
    val hv43: String?, // 화상전용 처치실
)

data class HospMedInfo(
    val userId: String,
    var dutyDstr1Cd: String,
    val ocpCd: String,
    val userNm: String,
    var ptTypeCd: String,
    var jobCd: String,
    var authCd: String,
    val rgstDttm: Instant,
    val updtDttm: Instant,
    val userStatCd: UserStatCd,
) {
    val userStatCdNm: String
        get() = userStatCd.cdNm

    val ptTypeCdNm: String
        get() = ptTypeCd.replace(";", ", ")

    init {
        dutyDstr1Cd = SidoCd.valueOf("SIDO$dutyDstr1Cd").cdNm
        jobCd = PmgrTypeCd.valueOf(jobCd).cdNm
        authCd = DtpmTypeCd.valueOf(authCd).cdNm
    }
}