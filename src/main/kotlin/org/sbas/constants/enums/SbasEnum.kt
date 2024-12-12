package org.sbas.constants.enums

interface SbasEnum {
    val code: String
    val cdNm: String
}

/**
 * 사용자 상태 코드
 */
enum class UserStatCd(val cdNm: String) {
    URST0001("가입 승인 대기"), URST0002("가입 승인"), URST0003("가입 반려"), URST0004("휴면"),
    URST0005("탈퇴 (본인)"), URST0006("탈퇴 (관리자)"), URST0007("잠김")
}

/**
 * 병상배정상태 코드
 */
enum class BedStatCd(val cdNm: String) {
    BAST0001("배정없음"), BAST0002("역학조사"), BAST0003("승인대기"),
    BAST0004("배정대기"), BAST0005("이송대기"), BAST0006("이송중"),
    BAST0007("완료"), BAST0008("배정불가"),
}

/**
 * 환자 유형 코드
 */
enum class PtTypeCd(val cdNm: String) {
    PTTP0001("일반"), PTTP0002("소아"), PTTP0003("투석"), PTTP0004("산모"),
    PTTP0005("수술"), PTTP0006("인공호흡기 사용"), PTTP0007("적극적 치료요청"), PTTP0008("신생아")
}

/**
 * 중증도 입력 유형 코드
 */
enum class SvrtIptTypeCd(val cdNm: String) {
  SVIP0001("직접선택"),
  SVIP0002("생체정보입력"),
}

/**
 * 중증 유형 코드
 */
enum class SvrtTypeCd(val cdNm: String) {
    SVTP0001("무증상"),
    SVTP0002("경증"),
    SVTP0003("중등증"),
    SVTP0004("준중증"),
    SVTP0005("중증"),
    SVTP0006("위중증"),
    SVTP0007("미분류"),
}

/**
 * 기저질환 유형 코드
 */
enum class UndrDsesCd(override val code: String, override val cdNm: String) : SbasEnum {
    UDDS0001("UDDS0001", "고혈압"),
    UDDS0002("UDDS0002", "당뇨"),
    UDDS0003("UDDS0003", "고지혈증"),
    UDDS0004("UDDS0004", "심혈관"),
    UDDS0005("UDDS0005", "뇌혈관"),
    UDDS0006("UDDS0006", "암"),
    UDDS0007("UDDS0007", "만성폐질환"),
    UDDS0008("UDDS0008", "폐렴"),
    UDDS0009("UDDS0009", "신장질환"),
    UDDS0010("UDDS0010", "정신질환"),
    UDDS0011("UDDS0011", "결핵"),
    UDDS0012("UDDS0012", "천식등알레르기"),
    UDDS0013("UDDS0013", "면역력저하자"),
    UDDS0014("UDDS0014", "기타"),
}

enum class TimeLineStatCd(val cdNm: String) {
    COMPLETE("complete"), SUSPEND("suspend"), CLOSED("closed")
}

enum class ReqBedTypeCd(val cdNm: String) {
    BDTP0001("미분류"),
    BDTP0002("일반격리"),
    BDTP0003("음압격리"),
    BDTP0004("소아일반격리"),
    BDTP0005("소아음압격리"),
    BDTP0006("일반"),
    BDTP0007("소아"),
}

enum class BnrnTypeCd(val cdNm: String) {
    BNRN0001("중환자실 부족"),
    BNRN0002("일반병실 부족"),
    BNRN0003("응급수술 불가"),
    BNRN0004("의료인 부족"),
    BNRN0005("응급실 과밀화3"),
    BNRN0006("장비 부족"),
    BNRN0007("기타"),
}

enum class AdmsStatCd(val cdNm: String) {
  IOST0001("입원"),
  IOST0002("퇴원"),
  IOST0003("자택회송"),
}

enum class DprtTypeCd(val cdNm: String) {
    DPTP0001("자택"),
    DPTP0002("병원"),
    DPTP0003("기타"),
}

enum class PmgrTypeCd(val cdNm: String) {
    PMGR0001("병상요청"),
    PMGR0002("병상승인"),
    PMGR0003("병상배정"),
    PMGR0004("시스템관리"),
}

enum class InstTypeCd(val cdNm: String) {
    ORGN0001("병상배정반"),
    ORGN0002("구급대"),
    ORGN0003("보건소"),
    ORGN0004("의료기관"),
    ORGN0005("전산담당"),
}

enum class DtpmTypeCd(val cdNm: String) {
    DTPM0001("일반"),
    DTPM0002("게스트"),
}

enum class DsbdType(val cdNm: String) {
  DBTP0001("지역"), 
  DBTP0002("기관"),
  DBTP0003("staff"),
  DBTP0004("병상배정상태"),
  DBTP0005("중증도"),
  DBTP0006("가용병상"),
}