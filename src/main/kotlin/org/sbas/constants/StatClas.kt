package org.sbas.constants

enum class StatClas(val value: String) {
    URST0001("가입 승인 대기"), URST0002("가입 승인"), URST0003("가입 반려"), URST0004("휴면"),
    URST0005("탈퇴 (본인)"), URST0006("탈퇴 (관리자)"), URST0007("잠김")
}
