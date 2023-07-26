package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.svrt.*
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SvrtPtRepository : PanacheRepositoryBase<SvrtPt, SvrtPtId> {

}

@ApplicationScoped
class SvrtAnlyRepository : PanacheRepositoryBase<SvrtAnly, SvrtAnlyId> {

    fun getLastAnlySeqValue(): Int? {
        val query = "select MAX(anly_seq) from svrt_anly"
        return getEntityManager().createNativeQuery(query).singleResult as Int?
    }
}

@ApplicationScoped
class SvrtCollRepository : PanacheRepositoryBase<SvrtColl, SvrtCollId> {

    /**
     * Get list of rows from svrt_coll table for last 4 days by fields msre_dt and pid
     */
    fun findByPtIdAndMsreDt(pid: String, date: String = Date(System.currentTimeMillis()).toString()): List<SvrtColl>? {
        return find("select sc from SvrtColl sc where sc.pid = '$pid' and (date(sc.id.msreDt) <= date('$date') and date(sc.id.msreDt) > (date('$date') - 4))").list()
    }

}







