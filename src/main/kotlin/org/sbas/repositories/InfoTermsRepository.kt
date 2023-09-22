package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.info.InfoPt
import org.sbas.entities.info.InfoTerms
import org.sbas.entities.info.InfoTermsId
import java.lang.Integer.parseInt
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class InfoTermsRepository : PanacheRepositoryBase<InfoTerms, InfoTermsId> {

    fun findByTermsType(termsType: String): InfoTerms? {
        var resultList = find("terms_type = '$termsType'").list()

        return resultList.maxByOrNull { it.id.termsVersion?.toIntOrNull() ?: 0 }
    }

}