package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import org.sbas.dtos.BdasListDto
import org.sbas.entities.bdas.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BdasEsvyRepository: PanacheRepositoryBase<BdasEsvy, String> {
    fun findByPtIdWithLatestBdasSeq(ptId: String): BdasEsvy? {
        return find("pt_id = '${ptId}'", Sort.by("bdas_seq", Sort.Direction.Descending)).firstResult()
    }
}

@ApplicationScoped
class BdasReqRepository : PanacheRepositoryBase<BdasReq, BdasReqId> {
    fun findByPtIdAndBdasSeq(ptId: String, bdasSeq: Int): BdasReq? {
        return find("pt_id = '${ptId}' and bdas_seq = $bdasSeq", Sort.by("bdas_seq", Sort.Direction.Descending)).firstResult()
    }

    fun findBdasReqList(): List<BdasListDto> {
        val query = "select new org.sbas.dtos.BdasListDto(br.id.ptId, br.id.bdasSeq, pt.ptNm, pt.gndr, fn_get_age(pt.rrno1, pt.rrno2), " +
                "pt.bascAddr, br.updtDttm, be.diagNm, fn_get_bed_asgn_stat(br.id.ptId, br.id.bdasSeq), '', br.ptTypeCd, br.svrtTypeCd, br.undrDsesCd) " +
                "from BdasReq br " +
                "join InfoPt pt on br.id.ptId = pt.ptId " +
                "join BdasEsvy be on br.id.bdasSeq = be.bdasSeq " +
                "where br.id.bdasSeq in (select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId) " +
                "order by br.updtDttm"
        return getEntityManager().createQuery(query, BdasListDto::class.java).resultList
    }
}

@ApplicationScoped
class BdasTrnRepository: PanacheRepositoryBase<BdasTrns, BdasTrnsId>

@ApplicationScoped
class BdasAprvRepository: PanacheRepositoryBase<BdasAprv, BdasAprvId>

@ApplicationScoped
class BdasAdmRepository: PanacheRepositoryBase<BdasAdms, BdasAdmsId>