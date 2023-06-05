package org.sbas.constants

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
    BAST0004("배정대기"), BAST0005("병상승인"), BAST0006("이송중"), BAST0007("완료")
}

/**
 * 환자 유형 코드
 */
enum class PtTypeCd(val cdNm: String) {
    PTTP0001("일반"), PTTP0002("소아"), PTTP0003("투석"), PTTP0004("산모"),
    PTTP0005("수술"), PTTP0006("중환자"),
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
    SVTP0007("사망"),
}

/**
 * 기저질환 유형 코드
 */
enum class UndrDsesCd(val cdNm: String) {
    UDDS0001("고혈압"),
    UDDS0002("당뇨"),
    UDDS0003("고지혈증"),
    UDDS0004("심혈관"),
    UDDS0005("뇌혈관"),
    UDDS0006("암"),
    UDDS0007("만성폐질환"),
    UDDS0008("폐렴"),
    UDDS0009("신장질환"),
    UDDS0010("정신질환"),
    UDDS0011("결핵"),
    UDDS0012("천식등알레르기"),
    UDDS0013("면역력저하자"),
    UDDS0014("기타"),
}
