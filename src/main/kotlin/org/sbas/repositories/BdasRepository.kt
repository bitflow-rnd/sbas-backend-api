package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
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
}

@ApplicationScoped
class BdasTrnRepository: PanacheRepositoryBase<BdasTrns, BdasTrnsId>

@ApplicationScoped
class BdasAprvRepository: PanacheRepositoryBase<BdasAprv, BdasAprvId>

@ApplicationScoped
class BdasAdmRepository: PanacheRepositoryBase<BdasAdms, BdasAdmsId>