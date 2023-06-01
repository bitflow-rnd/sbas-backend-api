package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import org.sbas.dtos.bdas.BdasListDto
import org.sbas.dtos.bdas.BdasTimeLineDto
import org.sbas.entities.bdas.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BdasEsvyRepository : PanacheRepositoryBase<BdasEsvy, String> {
    fun findByPtIdWithLatestBdasSeq(ptId: String): BdasEsvy? {
        return find("pt_id = '${ptId}'", Sort.by("bdas_seq", Sort.Direction.Descending)).firstResult()
    }
}

@ApplicationScoped
class BdasReqRepository : PanacheRepositoryBase<BdasReq, BdasReqId> {
    fun findByPtIdAndBdasSeq(ptId: String, bdasSeq: Int): BdasReq? {
        return find("pt_id = '${ptId}' and bdas_seq = $bdasSeq", Sort.by("bdas_seq", Sort.Direction.Descending)).firstResult()
    }

    fun findBdasReqList(): MutableList<BdasListDto> {
        val query = "select new org.sbas.dtos.bdas.BdasListDto(br.id.ptId, br.id.bdasSeq, pt.ptNm, pt.gndr, fn_get_age(pt.rrno1, pt.rrno2), " +
                "pt.bascAddr, br.updtDttm, be.diagNm, fn_get_bed_asgn_stat(br.id.ptId, br.id.bdasSeq), '', be.rcptPhc, br.ptTypeCd, br.svrtTypeCd, br.undrDsesCd) " +
                "from BdasReq br " +
                "join InfoPt pt on br.id.ptId = pt.ptId " +
                "join BdasEsvy be on br.id.bdasSeq = be.bdasSeq " +
                "where br.id.bdasSeq in (select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId) " +
                "order by br.updtDttm desc"
        return getEntityManager().createQuery(query, BdasListDto::class.java).resultList
    }

    fun findTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasTimeLineDto> {
        val query = "select new org.sbas.dtos.bdas.BdasTimeLineDto('병상요청(' || (case br.inhpAsgnYn when 'Y' then '원내배정' when 'N' then '전원요청' end) || ')', " +
                "iu.instNm || ' / ' || iu.userNm, br.updtDttm, br.msg, " +
                "br.inhpAsgnYn, iu.jobCd, iu.ocpCd, " +
                "(select instNm from InfoInst where id = 'LG00000001')) " +
                "from BdasReq br " +
                "join InfoUser iu on iu.id = br.rgstUserId " +
                "where br.id.ptId = '$ptId' and br.id.bdasSeq = $bdasSeq"
        return getEntityManager().createQuery(query, BdasTimeLineDto::class.java).resultList
    }

    fun findBedStat(ptId: String, bdasSeq: Int): String {
        val query = "select fn_get_bed_asgn_stat('${ptId}', ${bdasSeq}) as test"
        return getEntityManager().createNativeQuery(query).singleResult as String
    }

    fun findByPtId(ptId: String) = find("from BdasReq where id.ptId='$ptId' order by id.bdasSeq desc").firstResult()
}

@ApplicationScoped
class BdasReqAprvRepository : PanacheRepositoryBase<BdasReqAprv, BdasReqAprvId> {
    fun findTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasTimeLineDto> {
        val query = "select new org.sbas.dtos.bdas.BdasTimeLineDto(bra.aprvYn, " +
                "iu.instNm || ' / ' || iu.userNm, bra.updtDttm, bra.msg) " +
                "from BdasReqAprv bra " +
                "join InfoUser iu on iu.instId = bra.reqHospId"
        return getEntityManager().createQuery(query, BdasTimeLineDto::class.java).resultList
    }

    fun findReqAprvList(ptId: String, bdasSeq: Int): List<BdasReqAprv> {
        return find("id.ptId = '$ptId' and id.bdasSeq = $bdasSeq").list()
    }
}


@ApplicationScoped
class BdasAprvRepository: PanacheRepositoryBase<BdasAprv, BdasAprvId> {

    fun findApprovedEntity(ptId: String, bdasSeq: Int): BdasAprv? {
        return find("id.ptId = '$ptId' and id.bdasSeq = $bdasSeq and aprvYn = 'Y'").firstResult()
    }
}

@ApplicationScoped
class BdasTrnsRepository: PanacheRepositoryBase<BdasTrns, BdasTrnsId>

@ApplicationScoped
class BdasAdmsRepository: PanacheRepositoryBase<BdasAdms, BdasAdmsId>