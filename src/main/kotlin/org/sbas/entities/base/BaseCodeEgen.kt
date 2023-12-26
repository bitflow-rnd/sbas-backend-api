package org.sbas.entities.base

import org.sbas.entities.CommonEntity
import java.io.Serializable
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * E-GEN 기초 코드
 */
@Entity
@Table(name = "base_code_egen")
class BaseCodeEgen(
    @EmbeddedId
    var id: BaseCodeEgenId,

    @Size(max = 100)
    @NotNull
    @Column(name = "cm_mnm", nullable = false, length = 100)
    var cmMnm: String, // 코드 이름

    @Size(max = 100)
    @NotNull
    @Column(name = "cm_snm", nullable = false, length = 100)
    var cmSnm: String, // 코드 약어
) : CommonEntity()

@Embeddable
data class BaseCodeEgenId(
    @Size(max = 10)
    @NotNull
    @Column(name = "cm_mid", nullable = false, length = 10)
    var cmMid: String, // 대분류 코드

    @Size(max = 10)
    @NotNull
    @Column(name = "cm_sid", nullable = false, length = 10)
    var cmSid: String, // 소분류 코드
) : Serializable {
    companion object {
        private const val serialVersionUID = 8837760559231563619L
    }
}