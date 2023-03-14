package org.sbas.entities.base

import org.sbas.entities.CommonEntity
import java.time.Instant
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * E-GEN 기초 코드
 */
@Entity
@Table(name = "base_code_egen")
//@IdClass(BaseCodeEgenId::class)
class BaseCodeEgen : CommonEntity() {

    @EmbeddedId
    var id: BaseCodeEgenId? = null

//    @Size(max = 10)
//    @NotNull
//    @Id
//    @Column(name = "cm_mid", nullable = false, length = 10, insertable = false, updatable = false)
//    var cmMid: String? = null
//
//    @Size(max = 10)
//    @NotNull
//    @Id
//    @Column(name = "cm_sid", nullable = false, length = 10, insertable = false, updatable = false)
//    var cmSid: String? = null
//
    @Size(max = 100)
    @NotNull
    @Column(name = "cm_mnm", nullable = false, length = 100)
    var cmMnm: String? = null // 코드 이름

    @Size(max = 100)
    @NotNull
    @Column(name = "cm_snm", nullable = false, length = 100)
    var cmSnm: String? = null // 코드 약어

}