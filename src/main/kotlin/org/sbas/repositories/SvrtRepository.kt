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
    val query = "select new org.sbas.dtos.SvrtPtSearchDto(sp.pid, sp.id.rgstSeq, ip.ptId, br.id.bdasSeq, ip.ptNm, ip.gndr, ip.rrno1, " +
      "ip.dstr1Cd, fn_get_cd_nm('SIDO', ip.dstr1Cd), ip.dstr2Cd, fn_get_cd_nm('SIDO'||ip.dstr1Cd, ip.dstr2Cd), " +
      "sp.id.hospId, ih.dutyName, ip.mpno, ip.natiCd, ip.natiNm, sp.updtDttm, " +
      "br.ptTypeCd, br.svrtTypeCd, br.undrDsesCd, " +
      "sp.monStrtDt, sp.monStrtDt, sp.monEndDt, null ) " +
      "from InfoPt ip " +
      "join SvrtPt sp on ip.ptId = sp.id.ptId " +
      "join BdasReq br on sp.id.ptId = br.id.ptId and sp.id.bdasSeq = br.id.bdasSeq " +
      "join InfoHosp ih on sp.id.hospId = ih.hospId " +
      "where br.bedStatCd = 'BAST0007' " +
      "and sp.id.rgstSeq = (select max(sp2.id.rgstSeq) from SvrtPt sp2 where sp2.id.ptId = sp.id.ptId) " +
      "$cond " +
      "order by sp.updtDttm desc "

    return entityManager.createQuery(query, SvrtPtSearchDto::class.java).resultList
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
    var cond = param.ptNm?.run { " and (ip.ptNm like '%$this%' " } ?: "and (1=1"
    cond += param.rrno1?.run { " or ip.rrno1 like '%$this%' " } ?: ""
    cond += param.mpno?.run { " or ip.mpno like '%$this%') " } ?: ") "
    cond += param.ptId?.run { " and ip.ptId like '%$this%' " } ?: ""

    cond += param.dstr1Cd?.run { " and ip.dstr1Cd like '%$this%' " } ?: ""
    cond += param.dstr2Cd?.run { " and ip.dstr2Cd like '%$this%' " } ?: ""
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