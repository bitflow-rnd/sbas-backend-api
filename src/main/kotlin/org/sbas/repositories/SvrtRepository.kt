package org.sbas.repositories

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.hibernate.extension.createQuery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import org.sbas.dtos.SvrtInfoRsps
import org.sbas.entities.svrt.*
import org.sbas.utils.StringUtils

@ApplicationScoped
class SvrtPtRepository : PanacheRepositoryBase<SvrtPt, SvrtPtId> {

  fun findByPtId(ptId: String): List<SvrtPt> {
    return find("id.ptId = ?1", ptId).list()
  }

  fun findByPid(pid: String): SvrtPt? {
    return find("pid = ?1", pid).firstResult()
  }

  fun findAllWithMaxRgstSeq(): List<SvrtPt> {
    return findAll().list()
      .groupBy { svrtPt -> svrtPt.id.ptId }
      .mapValues { entry -> entry.value.maxByOrNull { svrtPt -> svrtPt.id.rgstSeq }!! }
      .values
      .toList()
  }
}

@ApplicationScoped
class SvrtAnlyRepository : PanacheRepositoryBase<SvrtAnly, SvrtAnlyId> {

  @Inject
  private lateinit var entityManager: EntityManager

  @Inject
  private lateinit var context: JpqlRenderContext

  fun getLastAnlySeqValue(): Int? {
    val query = "select MAX(sa.id.anlySeq) from SvrtAnly sa"
    return getEntityManager().createQuery(query).singleResult as Int?
  }

  fun findAllByPtIdAndHospIdAndCollSeq(ptId: String, hospId: String): List<SvrtAnly> {
    val query = "select sa from SvrtAnly sa where sa.id.ptId = '$ptId' and sa.id.hospId = '$hospId' and " +
      "sa.id.anlySeq = (select max(sa2.id.anlySeq) from SvrtAnly sa2 where sa2.id.ptId = '$ptId' and sa2.id.hospId = '$hospId') " +
      "order by sa.id.collSeq asc "
    return getEntityManager().createQuery(query, SvrtAnly::class.java).resultList
  }

  /**
   * Get data from last analysis by ptId
   */
  fun getSvrtAnlyByPtId(ptId: String): MutableList<*> {
    val query = "select pt_id, hosp_id, anly_dt, msre_dt, prdt_dt, svrt_prob_mean, svrt_prob_std, covsf " +
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
      "svrtProbMean" to row[5].toString(),
      "svrtProbStd" to row[6].toString(),
      "CovSF" to row[7].toString(),
    )
    }.toList()
  }

  fun getSvrtInfo(ptId: String): MutableList<SvrtInfoRsps>? {
    val query = jpql {
      selectNew<SvrtInfoRsps>(
        path(SvrtAnly::id)(SvrtAnlyId::ptId), path(SvrtAnly::id)(SvrtAnlyId::hospId), path(SvrtAnly::id)(SvrtAnlyId::anlyDt),
        path(SvrtAnly::id)(SvrtAnlyId::msreDt), path(SvrtAnly::prdtDt),
        path(SvrtAnly::covSf), path(SvrtColl::oxygenApply),
      ).from(
        entity(SvrtAnly::class),
        join(SvrtColl::class).on(
          path(SvrtAnly::id)(SvrtAnlyId::ptId).eq(path(SvrtColl::id)(SvrtCollId::ptId))
            .and(path(SvrtAnly::id)(SvrtAnlyId::hospId).eq(path(SvrtColl::id)(SvrtCollId::hospId)))
            .and(path(SvrtAnly::id)(SvrtAnlyId::collSeq).eq(path(SvrtColl::id)(SvrtCollId::collSeq)))
        ),
      ).whereAnd(
        path(SvrtAnly::id)(SvrtAnlyId::ptId).eq(ptId),
      ).orderBy(
        path(SvrtAnly::id)(SvrtAnlyId::msreDt).asc(),
        path(SvrtAnly::prdtDt).asc().nullsFirst(),
      )
    }

    return entityManager.createQuery(query, context).resultList
  }

  fun findMaxAnlySeqByPtIdAndPid(ptId: String, pid: String): SvrtAnly? {
    return find("id.ptId = ?1 and pid = ?2", Sort.by("id.anlySeq", Sort.Direction.Descending), ptId, pid).firstResult()
  }

  /**
   * Get data from last analysis of severity for current patient by his pt_id
   */
  fun getLastSvrtAnlyByPtId(ptId: String): MutableList<*> {

    val query = "select pt_id, hosp_id, anly_dt, msre_dt, prdt_dt, svrt_prob_mean, svrt_prob_std " +
      "from (select distinct on (sa.rgst_seq) sa.* " +
      "from svrt_anly as sa order by sa.rgst_seq, sa.anly_seq desc, sa.coll_seq) as first " +
      "where pt_id = '$ptId' " +
      "union " +
      "select pt_id, hosp_id, anly_dt, msre_dt, prdt_dt, svrt_prob_mean, svrt_prob_std " +
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

  fun findByPtIdAndPidOrderByRsltDtAsc(ptId: String, pid: String): List<SvrtColl> {
    val today = StringUtils.getYyyyMmDd()
    return find("select sc from SvrtColl sc where sc.id.ptId = '$ptId' and sc.pid = '$pid' and sc.rsltDt <= '$today' order by sc.rsltDt").list()
  }

  fun findAllByPtIdOrderByCollSeqAsc(ptId: String): List<SvrtColl> {
    return find("id.ptId = ?1", Sort.by("id.collSeq", Sort.Direction.Ascending), ptId).list()
  }

  fun findByPtIdAndHospId(ptId: String, hospId: String): List<SvrtColl> {
    return find("id.ptId = ?1 and id.hospId = ?2", ptId, hospId).list()
  }

  fun findByPidAndHospId(pid: String, hospId: String): List<SvrtColl> {
    return find("pid = ?1 and id.hospId = ?2", pid, hospId).list()
  }
}