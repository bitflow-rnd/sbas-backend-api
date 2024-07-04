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
import org.sbas.dtos.SvrtPtSearchDto
import org.sbas.dtos.info.InfoPtSearchParam
import org.sbas.entities.svrt.*
import org.sbas.utils.StringUtils

@ApplicationScoped
class SvrtPtRepository : PanacheRepositoryBase<SvrtPt, SvrtPtId> {

  @Inject
  private lateinit var entityManager: EntityManager

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

  fun findSvrtPtList(param: InfoPtSearchParam): List<SvrtPtSearchDto> {
    val cond = searchCondition(param)
    val today = StringUtils.getYyyyMmDd()
    val query = "select new org.sbas.dtos.SvrtPtSearchDto(pt.ptId, br.id.bdasSeq, pt.ptNm, pt.gndr, pt.rrno1, " +
      "pt.dstr1Cd, fn_get_cd_nm('SIDO', pt.dstr1Cd), pt.dstr2Cd, fn_get_cd_nm('SIDO'||pt.dstr1Cd, pt.dstr2Cd), " +
      "bap.hospId, ih.dutyName, pt.mpno, pt.natiCd, pt.natiNm, sa.updtDttm, " +
      "br.ptTypeCd, br.svrtTypeCd, br.undrDsesCd, " +
      "ip.monStrtDt, null ) " +
      "from InfoPt pt " +
      "join SvrtPt ip on pt.ptId = ip.id.ptId " +
      "left join BdasReq br on pt.ptId = br.id.ptId " +
      "left join BdasAprv bap on (br.id.bdasSeq = bap.id.bdasSeq and bap.aprvYn = 'Y') " +
      "left join InfoHosp ih on bap.hospId = ih.hospId " +
      "left join SvrtAnly sa on ip.id.ptId = sa.id.ptId and ip.pid = sa.pid and (sa.id.msreDt = '$today' and sa.prdtDt is null) " +
      "and sa.id.anlySeq = (select max(sa2.id.anlySeq) from SvrtAnly sa2 where sa2.id.ptId = pt.ptId and sa2.id.hospId = bap.hospId) " +
      "where (br.id.bdasSeq in ((select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId)) or br.id.bdasSeq is null) " +
      "and br.bedStatCd = 'BAST0007' " +
      "$cond " +
      "order by sa.updtDttm desc "

    val offset = param.page?.run { this.minus(1).times(15) } ?: 0
    return entityManager.createQuery(query, SvrtPtSearchDto::class.java).setMaxResults(15)
      .setFirstResult(offset).resultList
  }

  fun countSvrtPtList(param: InfoPtSearchParam): Long {
    val cond = searchCondition(param)
    val today = StringUtils.getYyyyMmDd()
    val query = "select count(pt.ptId) " +
      "from InfoPt pt " +
      "join SvrtPt ip on pt.ptId = ip.id.ptId " +
      "left join BdasReq br on pt.ptId = br.id.ptId " +
      "left join BdasAprv bap on (br.id.bdasSeq = bap.id.bdasSeq and bap.aprvYn = 'Y') " +
      "left join InfoHosp ih on bap.hospId = ih.hospId " +
      "left join SvrtAnly sa on ip.id.ptId = sa.id.ptId and ip.pid = sa.pid and (sa.id.msreDt = '$today' and sa.prdtDt is null) " +
      "and sa.id.anlySeq = (select max(sa2.id.anlySeq) from SvrtAnly sa2 where sa2.id.ptId = pt.ptId and sa2.id.hospId = bap.hospId) " +
      "where (br.id.bdasSeq in ((select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId)) or br.id.bdasSeq is null) " +
      "and br.bedStatCd = 'BAST0007' " +
      "$cond "

    return entityManager.createQuery(query).singleResult as Long
  }

  private fun searchCondition(param: InfoPtSearchParam): String {
    var cond = param.ptNm?.run { " and (pt.ptNm like '%$this%' " } ?: "and (1=1"
    cond += param.rrno1?.run { " or pt.rrno1 like '%$this%' " } ?: ""
    cond += param.mpno?.run { " or pt.mpno like '%$this%') " } ?: ") "
    cond += param.ptId?.run { " and pt.ptId like '%$this%' " } ?: ""

    cond += param.dstr1Cd?.run { " and pt.dstr1Cd like '%$this%' " } ?: ""
    cond += param.dstr2Cd?.run { " and pt.dstr2Cd like '%$this%' " } ?: ""
    cond += param.hospNm?.run { " and ih.dutyName like '%$this%' " } ?: ""
    return cond
  }
}

@ApplicationScoped
class SvrtAnlyRepository : PanacheRepositoryBase<SvrtAnly, SvrtAnlyId> {

  @Inject
  private lateinit var entityManager: EntityManager

  @Inject
  private lateinit var context: JpqlRenderContext

  fun findAllByPtIdAndHospIdAndCollSeq(ptId: String, hospId: String): List<SvrtAnly> {
    val query = "select sa from SvrtAnly sa where sa.id.ptId = '$ptId' and sa.id.hospId = '$hospId' and " +
      "sa.id.anlySeq = (select max(sa2.id.anlySeq) from SvrtAnly sa2 where sa2.id.ptId = '$ptId' and sa2.id.hospId = '$hospId') " +
      "order by sa.id.collSeq asc "
    return getEntityManager().createQuery(query, SvrtAnly::class.java).resultList
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