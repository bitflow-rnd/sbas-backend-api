package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.jboss.logging.Logger
import org.sbas.entities.info.InfoTerms
import org.sbas.entities.info.InfoTermsId
import org.sbas.entities.info.TermsAgreement
import org.sbas.entities.info.TermsAgreementId
import org.sbas.responses.terms.AgreeTermsListResponse
import java.util.Comparator.comparing
import java.util.stream.Collectors.groupingBy
import java.util.stream.Collectors.maxBy
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class InfoTermsRepository : PanacheRepositoryBase<InfoTerms, InfoTermsId> {

    fun findTermsVersionByTermsType(termsType: String): String {
        val resultList = find("id.termsType = '$termsType'").list()

        val maxVersion = resultList.maxByOrNull { it.id.termsVersion.toIntOrNull() ?: 0 }
        return maxVersion?.id?.termsVersion ?: "00"
    }

    fun findTermsListByTermsType(termsType: String): List<InfoTerms> {
        return find("id.termsType = '$termsType'").list()
    }

    fun findTermsByTermsTypeAndTermsVersion(termsType: String, termsVersion: String): InfoTerms? {
        return find("id.termsType = '$termsType' and id.termsVersion = '$termsVersion'").firstResult()
    }

    fun findRecentTermsByTermsType(termsType: String): InfoTerms {
        return find("id.termsType = '$termsType'")
                .stream()
                .collect(maxBy(comparing { it.id.termsVersion }))
                .get()
    }

}

@ApplicationScoped
class TermsAgreementRepository : PanacheRepositoryBase<TermsAgreement, TermsAgreementId> {

    @Inject
    private lateinit var infoTermsRepository: InfoTermsRepository

    @Inject
    private lateinit var log: Logger

    fun findAgreeTermsListByUserId(userId: String, termsType: String): List<AgreeTermsListResponse> {
        val result = mutableListOf<AgreeTermsListResponse>()

        if (termsType == "00") {
            val latestAgreeTerms =
                find("id.userId = '$userId' and agreeYn = 'Y'")
                    .stream()
                    .collect(groupingBy({ it.id.termsType }, maxBy(comparing { it.id.termsVersion })))
                    ?: return result

            latestAgreeTerms.forEach { (key, value) ->
                val latestVersion = infoTermsRepository.findTermsVersionByTermsType(key)
                val agreeTerms = infoTermsRepository.findTermsByTermsTypeAndTermsVersion(key, value.get().id.termsVersion)
                    ?: return@forEach

                val item = AgreeTermsListResponse(
                    userId = userId,
                    termsType = key,
                    termsName = agreeTerms.termsName!!,
                    recentYn = if (value.get().id.termsVersion == latestVersion) "Y" else "N",
                    detail = agreeTerms.detail,
                    agreeDttm = latestAgreeTerms[key]?.get()?.agreeDt!! + latestAgreeTerms[key]?.get()?.agreeTm!!
                )
                result.add(item)
            }
        } else {
            val latestAgreeTerms =
                find("id.userId = '$userId' and id.termsType = '$termsType' and agreeYn = 'Y'")
                    .stream()
                    .collect(maxBy(comparing { it.id.termsVersion }))
                    ?: return result

            val latestVersion = infoTermsRepository.findTermsVersionByTermsType(termsType)
            val infoTerms = infoTermsRepository.findTermsByTermsTypeAndTermsVersion(termsType, latestAgreeTerms.get().id.termsVersion)

            if (infoTerms == null) return result
            else {
                val item = AgreeTermsListResponse(
                    userId = userId,
                    termsType = termsType,
                    termsName = infoTerms.termsName!!,
                    recentYn = if (latestAgreeTerms.get().id.termsVersion == latestVersion) "Y" else "N",
                    detail = infoTerms.detail,
                    agreeDttm = latestAgreeTerms.get().agreeDt!! + latestAgreeTerms.get().agreeTm!!
                )
                result.add(item)
            }
        }
        return result
    }

}