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

    fun findTermsByTermsTypeAndTermsVersion(termsType: String, termsVersion: String): InfoTerms? {
        return find("terms_type = '$termsType' and terms_version = '$termsVersion'").firstResult()
    }

}

@ApplicationScoped
class TermsAgreementRepository : PanacheRepositoryBase<TermsAgreement, TermsAgreementId> {

    @Inject
    private lateinit var infoTermsRepository: InfoTermsRepository

    fun findAgreeTermsListByUserId(userId: String, termsType: String): List<AgreeTermsListResponse> {
        val result = mutableListOf<AgreeTermsListResponse>()

        if (termsType == "00") {
            val latestAgreeTerms =
                find("user_id = '$userId' and agree_yn = 'Y'")
                    .stream()
                    .collect(groupingBy({ it.id.termsType }, maxBy(comparing { it.id.termsVersion })))

            latestAgreeTerms.forEach{(key, value) ->
                val latestVersion = infoTermsRepository.findTermsVersionByTermsType(key)
                val agreeTerms = infoTermsRepository.findTermsByTermsTypeAndTermsVersion(key, value.get().id.termsVersion)
                    ?: return@forEach

                val item = AgreeTermsListResponse(
                    userId = userId,
                    termsType = key,
                    termsName = agreeTerms.termsName!!,
                    recentYn = if (value.get().id.termsVersion == latestVersion) "Y" else "N",
                    detail = agreeTerms.detail,
                    agreeDttm = agreeTerms.updtDttm
                )
                result.add(item)
            }
        } else {
            val latestAgreeTerms =
                find("user_id = '$userId' and terms_type = '$termsType' and agree_yn = 'Y'")
                    .stream()
                    .collect(maxBy(comparing { it.id.termsVersion }))

            val latestVersion = infoTermsRepository.findTermsVersionByTermsType(termsType)
            val infoTerms = infoTermsRepository.findTermsByTermsTypeAndTermsVersion(termsType, latestAgreeTerms.get().id.termsVersion)

            if(infoTerms == null) return result
            else {
                val item = AgreeTermsListResponse(
                    userId = userId,
                    termsType = termsType,
                    termsName = infoTerms.termsName!!,
                    recentYn = if(latestAgreeTerms.get().id.termsVersion == latestVersion) "Y" else "N",
                    detail = infoTerms.detail,
                    agreeDttm = infoTerms.updtDttm
                )
                result.add(item)
            }
        }
        return result
    }

}