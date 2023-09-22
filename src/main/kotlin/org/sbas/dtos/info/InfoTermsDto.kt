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
    fun toEntity(): InfoTerms{
        val id = InfoTermsId(termsType = termsType)
        var termsName = ""

        if(termsType == "01") termsName = "개인정보수집동의"
        else if(termsType == "02") termsName = "서비스이용약관"
        else if(termsType == "03") termsName = "개인정보처리방침"

        return InfoTerms(
            id = id,
            termsName = termsName,
            detail = detail)
    }
}