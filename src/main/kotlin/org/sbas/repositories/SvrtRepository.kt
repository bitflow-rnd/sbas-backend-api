package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.svrt.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SvrtPtRepository : PanacheRepositoryBase<SvrtPt, SvrtPtId> {

}

@ApplicationScoped
class SvrtAnlyRepository : PanacheRepositoryBase<SvrtAnly, SvrtAnlyId> {

}

@ApplicationScoped
class SvrtCollRepository : PanacheRepositoryBase<SvrtColl, SvrtCollId> {

    fun findByPtIdAndMsreDt(pid: String): List<SvrtColl>? {
        return find("select sc from SvrtColl sc where sc.pid = '$pid' and (date(sc.id.msreDt) < current_date and date(sc.id.msreDt) >= (current_date - 4))").list()
    }
}