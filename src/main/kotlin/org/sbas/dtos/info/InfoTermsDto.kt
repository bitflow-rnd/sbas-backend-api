package org.sbas.dtos.info

import org.hibernate.validator.constraints.Length
import org.sbas.entities.info.InfoTerms
import org.sbas.entities.info.InfoTermsId
import javax.validation.constraints.NotBlank

data class RegTermsReq(
    @field: [NotBlank(message = "약관 타입을 설정해 주세요.") Length(max = 2)]
    val termsType: String,
    val detail: String,
) {

    fun toEntity(version: String): InfoTerms {
        var id = InfoTermsId(termsType = termsType, termsVersion = version)

        var termsName = ""

        if (termsType == "01") termsName = "개인정보수집동의"
        else if (termsType == "02") termsName = "서비스이용약관"
        else if (termsType == "03") termsName = "개인정보처리방침"

        return InfoTerms(
            id = id,
            termsName = termsName,
            detail = detail,
        )
    }
}
data class ModTermsReq(
    @field: [NotBlank(message = "약관 타입을 설정해 주세요.") Length(max = 2)]
    val termsType: String,
    val termsVersion: String?,
    val detail: String,
)

data class DelTermsReq(
    @field: [NotBlank(message = "약관 타입을 설정해 주세요.") Length(max = 2)]
    val termsType: String,
    @field: [NotBlank(message = "약관 버전을 설정해 주세요.") Length(max = 2)]
    val termsVersion: String,
)

data class TermsAgreeReq(
    @field: [NotBlank(message = "사용자 ID를 입력해 주세요.") Length(max = 2)]
    val userId: String,
    @field: [NotBlank(message = "약관 타입을 설정해 주세요.") Length(max = 2)]
    val termsType: String,
    val agreeYn: String,
)