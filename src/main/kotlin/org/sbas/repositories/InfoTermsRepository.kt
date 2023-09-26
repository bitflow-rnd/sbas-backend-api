package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.info.InfoTerms
import org.sbas.entities.info.InfoTermsId
import org.sbas.entities.info.TermsAgreement
import org.sbas.entities.info.TermsAgreementId
import org.sbas.responses.terms.AgreeTermsListResponse
import java.util.Comparator.comparing
import java.util.stream.Collectors.groupingBy
import java.util.stream.Collectors.maxBy
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class InfoTermsRepository : PanacheRepositoryBase<InfoTerms, InfoTermsId> {

    fun findTermsVersionByTermsType(termsType: String): String {
        val resultList = find("terms_type = '$termsType'").list()

        val maxVersion = resultList.maxByOrNull { it.id.termsVersion?.toIntOrNull() ?: 0 }
        return maxVersion?.id?.termsVersion ?: "00"
    }

    fun findTermsListByTermsType(termsType: String): List<InfoTerms> {
        return find("terms_type = '$termsType'").list()
    }

}

@ApplicationScoped
class TermsAgreementRepository : PanacheRepositoryBase<TermsAgreement, TermsAgreementId> {

    @Inject
    private lateinit var infoTermsRepository: InfoTermsRepository

    fun findAgreeTermsListByUserId(userId: String, termsType: String): List<AgreeTermsListResponse> {
        val latestAgreedTerms = find("userId", userId, "termsType", termsType)
            .stream()
            .collect(groupingBy({ it.id.termsType }, maxBy(comparing { it.id.termsVersion })))

        return latestAgreedTerms.values.mapNotNull { latestAgreedTerm ->
            val infoTerm = infoTermsRepository.findById(InfoTermsId(latestAgreedTerm.get().id.termsType, latestAgreedTerm.get().id.termsVersion))
            infoTerm?.let {
                AgreeTermsListResponse(
                    userId = userId,
                    termsType = it.id.termsType,
                    termsName = it.termsName ?: "",
                    recentYn = if (latestAgreedTerm.get().agreeYn == "Y") "Y" else "N", // 동의 여부를 "Y" 또는 "N"으로 매핑
                    detail = it.detail,
                    agreeDttm = latestAgreedTerm.get().updtDttm
                )
            }
        }
    }

}