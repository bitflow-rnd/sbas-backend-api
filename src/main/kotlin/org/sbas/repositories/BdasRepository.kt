package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import org.sbas.entities.bdas.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BdasEsvyRepository: PanacheRepositoryBase<BdasEsvy, String> {

    fun findByPtIdWithLatestHistSeq(ptId: String): BdasEsvy? {
        return find("pt_id = '${ptId}' and hist_cd = 'Y'", Sort.by("hist_seq", Sort.Direction.Descending)).firstResult()
    }

    fun findLatestBdasSeq(): Int {
        return getEntityManager().createQuery("select max(bdasSeq) from BdasEsvy").singleResult as Int
    }

    fun findByPtIdWithLatestBdasSeq(ptId: String): BdasEsvy? {
        return find("pt_id = '${ptId}' and hist_cd = 'Y'", Sort.by("bdas_seq", Sort.Direction.Descending)).firstResult()
    }
}

@ApplicationScoped
class BdasReqRepository : PanacheRepositoryBase<BdasReq, BdasReqId> {
    fun isRecordExist(ptId: String, bdasSeq: Int): BdasReq? {
        return find("pt_id = '${ptId}' and bdas_seq = $bdasSeq and hist_cd = 'Y'", Sort.by("hist_seq", Sort.Direction.Descending)).firstResult()
    }
}

@ApplicationScoped
class BdasTrnRepository: PanacheRepositoryBase<BdasTrns, BdasTrnsId>

@ApplicationScoped
class BdasAprvRepository: PanacheRepositoryBase<BdasAprv, BdasAprvId>

@ApplicationScoped
class BdasAdmRepository: PanacheRepositoryBase<BdasAdms, BdasAdmsId>