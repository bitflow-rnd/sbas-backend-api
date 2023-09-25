package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.info.InfoTerms
import org.sbas.entities.info.InfoTermsId
import org.sbas.entities.info.TermsAgreement
import org.sbas.entities.info.TermsAgreementId
import javax.enterprise.context.ApplicationScoped

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

}