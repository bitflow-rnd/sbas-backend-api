package org.sbas.constants

enum class StatClas(val value: String) {
    URST0001("가입 승인 대기"), URST0002("가입 승인"), URST0003("가입 반려"), URST0004("휴면"),
    URST0005("탈퇴 (본인)"), URST0006("탈퇴 (관리자)"), URST0007("잠김")
}

/**
 * InfoPt 엔티티에서 사용하는 병상배정상태 코드
 */
enum class BedStat(val cdNm: String) {
    BAST0001("병상요청"), BAST0002("배정대기"), BAST0003("이송처리대기"),
    BAST0004("이송중"), BAST0005("완료")
}