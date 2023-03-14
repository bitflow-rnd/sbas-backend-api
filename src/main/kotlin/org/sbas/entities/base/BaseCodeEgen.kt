package org.sbas.entities.base

import org.sbas.entities.CommonEntity
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * E-GEN 기초 코드
 */
@Entity
@Table(name = "base_code_egen")
class BaseCodeEgen : CommonEntity() {

    @EmbeddedId
    var id: BaseCodeEgenId? = null

    @Size(max = 100)
    @NotNull
    @Column(name = "cm_mnm", nullable = false, length = 100)
    var cmMnm: String? = null // 코드 이름

    @Size(max = 100)
    @NotNull
    @Column(name = "cm_snm", nullable = false, length = 100)
    var cmSnm: String? = null // 코드 약어

}

@Embeddable
class BaseCodeEgenId : Serializable {

    @Size(max = 10)
    @NotNull
    @Column(name = "cm_mid", nullable = false, length = 10)
    var cmMid: String? = null // 대분류 코드

    @Size(max = 10)
    @NotNull
    @Column(name = "cm_sid", nullable = false, length = 10)
    var cmSid: String? = null // 소분류 코드

    companion object {
        private const val serialVersionUID = 8837760559231563619L
    }

}