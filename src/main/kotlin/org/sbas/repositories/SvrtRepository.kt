package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import org.sbas.dtos.SvrtPtSearchDto
import org.sbas.dtos.info.SvrtPtSearchParam
import org.sbas.entities.svrt.*
import org.sbas.utils.StringUtils

@ApplicationScoped
class SvrtPtRepository : PanacheRepositoryBase<SvrtPt, SvrtPtId> {

  @Inject
  private lateinit var entityManager: EntityManager

  fun findByPtId(ptId: String): List<SvrtPt> {
    return find("id.ptId = ?1", ptId).list()
  }

  fun findByPtIdAndRgstSeq(pid: String, rgstSeq: Int): SvrtPt? {
    return find("id.ptId = ?1 and id.rgstSeq = ?2", pid, rgstSeq).firstResult()
  }

  fun findAllWithMaxRgstSeq(): List<SvrtPt> {
    return findAll().list()
      .groupBy { svrtPt -> svrtPt.id.ptId }
      .mapValues { entry -> entry.value.maxByOrNull { svrtPt -> svrtPt.id.rgstSeq }!! }
      .values
      .toList()
  }

  fun findSvrtPtList(param: SvrtPtSearchParam): List<SvrtPtSearchDto> {
    val cond = searchCondition(param)
    val query = "select new org.sbas.dtos.SvrtPtSearchDto(ip.pid, ip.id.rgstSeq, pt.ptId, br.id.bdasSeq, pt.ptNm, pt.gndr, pt.rrno1, " +
      "pt.dstr1Cd, fn_get_cd_nm('SIDO', pt.dstr1Cd), pt.dstr2Cd, fn_get_cd_nm('SIDO'||pt.dstr1Cd, pt.dstr2Cd), " +
      "ip.id.hospId, ih.dutyName, pt.mpno, pt.natiCd, pt.natiNm, sa.updtDttm, " +
      "br.ptTypeCd, br.svrtTypeCd, br.undrDsesCd, " +
      "ip.monStrtDt, null ) " +
      "from InfoPt pt " +
      "join SvrtPt ip on pt.ptId = ip.id.ptId " +
      "left join BdasReq br on pt.ptId = br.id.ptId " +
      "left join BdasAprv bap on (br.id.bdasSeq = bap.id.bdasSeq and bap.aprvYn = 'Y') " +
      "left join InfoHosp ih on ip.id.hospId = ih.hospId " +
      "left join SvrtAnly sa on ip.id.ptId = sa.id.ptId and ip.pid = sa.pid " +
      "and sa.id.anlySeq = (select max(sa2.id.anlySeq) from SvrtAnly sa2 where sa2.id.ptId = pt.ptId and sa2.id.hospId = bap.hospId) " +
      "and sa.id.collSeq = 1 " +
      "where (br.id.bdasSeq in ((select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId)) or br.id.bdasSeq is null) " +
      "and br.bedStatCd = 'BAST0007' " +
      "$cond " +
      "order by sa.updtDttm desc "

    val offset = param.page?.run { this.minus(1).times(15) } ?: 0
    return entityManager.createQuery(query, SvrtPtSearchDto::class.java).setMaxResults(15)
      .setFirstResult(offset).resultList
  }

  fun countSvrtPtList(param: SvrtPtSearchParam): Long {
    val cond = searchCondition(param)
    val query = "select count(pt.ptId) " +
      "from InfoPt pt " +
      "join SvrtPt ip on pt.ptId = ip.id.ptId " +
      "left join BdasReq br on pt.ptId = br.id.ptId " +
      "left join BdasAprv bap on (br.id.bdasSeq = bap.id.bdasSeq and bap.aprvYn = 'Y') " +
      "left join InfoHosp ih on bap.hospId = ih.hospId " +
      "left join SvrtAnly sa on ip.id.ptId = sa.id.ptId and ip.pid = sa.pid " +
      "and sa.id.anlySeq = (select max(sa2.id.anlySeq) from SvrtAnly sa2 where sa2.id.ptId = pt.ptId and sa2.id.hospId = bap.hospId) " +
      "and sa.id.collSeq = 1 " +
      "where (br.id.bdasSeq in ((select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId)) or br.id.bdasSeq is null) " +
      "and br.bedStatCd = 'BAST0007' " +
      "$cond "

    return entityManager.createQuery(query).singleResult as Long
  }

  private fun searchCondition(param: SvrtPtSearchParam): String {
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
  fun findLastByPtIdAndHospId(ptId: String, hospId: String, rgstSeq: Int): List<SvrtAnly> {
    val query = "select sa from SvrtAnly sa where sa.id.ptId = '$ptId' and sa.id.hospId = '$hospId' and sa.id.rgstSeq = $rgstSeq and " +
      "sa.id.anlySeq = (select max(sa2.id.anlySeq) from SvrtAnly sa2 where sa2.id.ptId = '$ptId' and sa2.id.hospId = '$hospId' and sa2.id.rgstSeq = $rgstSeq) " +
      "order by sa.id.collSeq asc "
    return getEntityManager().createQuery(query, SvrtAnly::class.java).resultList
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

  fun findByPtIdOrderByRsltDtAsc(ptId: String): List<SvrtColl> {
    val today = StringUtils.getYyyyMmDd()
    return find("select sc from SvrtColl sc where sc.id.ptId = '$ptId' and sc.rsltDt <= '$today' order by sc.rsltDt").list()
  }

  fun findAllByPtIdOrderByCollSeqAsc(ptId: String, rgstSeq: Int): List<SvrtColl> {
    return find("id.ptId = ?1 and id.rgstSeq = ?2", Sort.by("id.collSeq", Sort.Direction.Ascending), ptId, rgstSeq).list()
  }

  fun findByPtIdAndHospId(ptId: String, hospId: String): List<SvrtColl> {
    return find("id.ptId = ?1 and id.hospId = ?2", ptId, hospId).list()
  }

  fun findByPtId(ptId: String): List<SvrtColl> {
    return find("id.ptId = ?1", ptId).list()
  }

  fun findByPtIdAndRgstSeq(ptId: String, rgstSeq: Int): List<SvrtColl> {
    return find("id.ptId = ?1 and id.rgstSeq = ?2", ptId, rgstSeq).list()
  }
}