package org.sbas.entities.info

import jakarta.persistence.*
import org.sbas.entities.CommonEntity
import java.io.Serializable

@Entity
@Table(name = "terms_agreement")
class TermsAgreement (

    @EmbeddedId
    var id: TermsAgreementId,

    @Column(name = "agree_yn", nullable = false)
    var agreeYn: String? = null,

    @Column(name = "agree_dt", nullable = false, length = 8)
    var agreeDt: String? = null,

    @Column(name = "agree_tm", nullable = false, length = 4)
    var agreeTm: String? = null,

) : CommonEntity()

@Embeddable
data class TermsAgreementId(
    @Column(name = "user_id", nullable = false, length = 15)
    var userId: String, // 사용자 아이디

    @Column(name = "terms_type", nullable = false, length = 2)
    var termsType: String, // 약관 타입

    @Column(name = "terms_version", nullable = false, length = 2)
    var termsVersion: String, // 약관 버전

) : Serializable {

    companion object {
        private const val serialVersionUID: Long = -2970006139843366471L
    }
}