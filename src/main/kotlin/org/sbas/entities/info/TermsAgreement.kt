package org.sbas.entities.info

import org.sbas.entities.CommonEntity
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "terms_agreement")
class TermsAgreement (

    @EmbeddedId
    var id: TermsAgreementId,

    @Column(name = "agree_yn", nullable = false)
    var agreeYn: String? = null,
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