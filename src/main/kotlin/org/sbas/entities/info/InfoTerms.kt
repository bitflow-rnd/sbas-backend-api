package org.sbas.entities.info

import jakarta.persistence.*
import org.sbas.entities.CommonEntity
import java.io.Serializable

@Entity
@Table(name = "info_terms")
class InfoTerms (

    @EmbeddedId
    var id: InfoTermsId,

    @Column(name = "terms_name", nullable = false, length = 20)
    var termsName: String? = null,

    @Column(name = "detail", nullable = false, columnDefinition = "TEXT")
    var detail: String,

) : CommonEntity()

@Embeddable
data class InfoTermsId(
    @Column(name = "terms_type", nullable = false, length = 2)
    var termsType: String, // 01 : 개인정보수집동의, 02 : 서비스이용약관, 03 : 개인정보처리방침

    @Column(name = "terms_version", nullable = false, length = 2)
    var termsVersion: String, // 약관 버전

    @Column(name = "effective_dt", nullable = false, length = 8)
    var effectiveDt: String,
) : Serializable {

    companion object {
        private const val serialVersionUID: Long = -7147081231999864822L
    }
}