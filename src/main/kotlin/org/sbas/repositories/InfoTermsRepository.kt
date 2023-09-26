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
        val result = mutableListOf<AgreeTermsListResponse>()
        val latestAgreeTerms =
            if (termsType == "00") {
                find("user_id = '$userId' and agree_yn = 'Y'")
                    .stream()
                    .collect(groupingBy({ it.id.termsType }, maxBy(comparing { it.id.termsVersion })))
            } else {
                find("user_id = '$userId' and terms_type = '$termsType' and agree_yn = 'Y'")
                    .stream()
                    .collect(groupingBy({ it.id.termsType }, maxBy(comparing { it.id.termsVersion })))
            }
        val latestVersionTerms =
            if (termsType == "00") {
                infoTermsRepository.findAll()
                    .stream()
                    .collect(
                        groupingBy(
                        { it.id.termsType },
                        maxBy(comparing { it.id.termsVersion.toString() })
                        )
                    )
            }else {
                infoTermsRepository.find("terms_type = '$termsType'")
                    .stream()
                    .collect(groupingBy({ it.id.termsType }, maxBy(comparing { it.id.termsVersion.toString() })))
            }

        latestVersionTerms.let {
            latestAgreeTerms.forEach { (key, value) ->
                val input = AgreeTermsListResponse(
                    userId = userId,
                    termsType = value.get().id.termsType,
                    termsName = it[key]?.get()?.termsName!!,
                    recentYn = if(it[key]?.get()?.id?.termsVersion == value.get().id.termsVersion) "Y" else "N",
                    detail = it[key]?.get()?.detail!!,
                    agreeDttm = value.get().updtDttm
                )
                result.add(input)
            }
        }

        return result
    }

}