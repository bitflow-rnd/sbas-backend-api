package org.sbas.utils

/**
 * 기본 생성자 만들어주는 Annotation -> build.gradle.kts에 있는 noArg 항목에 annotation("org.sbas.utils.NoArg") 로 추가했습니다.
 * 기본 생성자 직접 호출은 불가하다고 합니다.
 * -> The generated constructor is synthetic so it can't be directly called from Java or Kotlin, but it can be called using reflection.
 * https://kotlinlang.org/docs/no-arg-plugin.html
 *
 * DTO 나 Parameter 쓰실때 사용하시면 될 것 같습니다.
 */
@Target(AnnotationTarget.CLASS)
annotation class NoArg
