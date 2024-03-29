package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.dtos.info.InfoPtSearchDto
import org.sbas.entities.svrt.*
import java.util.*
import jakarta.enterprise.context.ApplicationScoped

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
     * Get data from last analysis by ptId
     */
    fun getSvrtAnlyByPtId(ptId: String): MutableList<*> {
        val query = "select pt_id, hosp_id, anly_dt, msre_dt, prdt_dt, CovSF " +
                "from svrt_anly as sa " +
                "where sa.anly_seq = (select max(anly_seq) from svrt_anly where pt_id = '$ptId') and pt_id = '$ptId' " +
                "order by sa.prdt_dt"
        val result = getEntityManager().createNativeQuery(query).resultList as MutableList<*>

        return result.stream().map { row ->
            row as Array<*>; mapOf<String, String>(
                "ptId" to row[0].toString(),
                "hospId" to row[1].toString(),
                "anlyDt" to row[2].toString(),
                "msreDt" to row[3].toString(),
                "prdtDt" to row[4].toString(),
                "CovSF" to row[5].toString(),
        )
        }.toList()
    }

    /**
     * Get data from last analysis of severity for current patient by his pt_id
     */
    fun getLastSvrtAnlyByPtId(ptId: String): MutableList<*> {

        val query = "select pt_id, hosp_id, anly_dt, msre_dt, prdt_dt, svrt_prob " +
                "from (select distinct on (sa.rgst_seq) sa.* " +
                "from svrt_anly as sa order by sa.rgst_seq, sa.anly_seq desc, sa.coll_seq) as first " +
                "where pt_id = '$ptId' " +
                "union " +
                "select pt_id, hosp_id, anly_dt, msre_dt, prdt_dt, svrt_prob " +
                "from svrt_anly as second " +
                "where second.msre_dt = (select max(msre_dt) from svrt_anly) and pt_id = '$ptId' " +
                "order by prdt_dt"

        val result = getEntityManager().createNativeQuery(query).resultList as MutableList<*>

        return result.stream().map { row ->
            row as Array<*>; mapOf<String, String>(
            "ptId" to row[0].toString(),
            "hospId" to row[1].toString(),
            "anlyDt" to row[2].toString(),
            "msreDt" to row[3].toString(),
            "prdtDt" to row[4].toString(),
            "svrtProb" to row[5].toString()
        )
        }.toList()
    }
}

@ApplicationScoped
class SvrtCollRepository : PanacheRepositoryBase<SvrtColl, SvrtCollId> {

    /**
     * Get list of rows from svrt_coll table by fields msre_dt and pid
     */
    fun findByPtIdAndMsreDt(pid: String): List<SvrtColl>? {
        return find("select sc from SvrtColl sc where sc.pid = '$pid'").list()
    }

}