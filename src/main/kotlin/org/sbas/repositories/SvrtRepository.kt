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
        val query = "select MAX(sa.id.anlySeq) from SvrtAnly sa"
        return getEntityManager().createQuery(query).singleResult as Int?
    }

    /**
     * Get data from last analysis of severity for current patient by his pt_id
     */
    fun getLastSvrtAnlyByPtId(ptId: String): MutableList<Any?> {
        val query = "select new map(sa.id.ptId as ptId, sa.id.hospId as hospId, sa.id.anlyDt as anlyDt, " +
                    "sa.id.msreDt as msreDt, sa.prdtDt as prdtDt, sa.svrtProb as svrtProb) " +
                    "from SvrtAnly sa " +
                    "where sa.id.anlySeq = (select max(sq.id.anlySeq) from SvrtAnly sq) and sa.id.ptId = '$ptId'"
        return getEntityManager().createQuery(query).resultList
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