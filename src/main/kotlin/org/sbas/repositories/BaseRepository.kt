package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import org.sbas.entities.base.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BaseCodeRepository : PanacheRepositoryBase<BaseCode, BaseCodeId> {

    fun findBaseCodeByCdGrpId(cdGrpId: String): List<BaseCode> = find("cd_grp_id = ?1", cdGrpId).list()

    fun findBaseCdGrpList(): List<BaseCode> {
        return find("select a.id.cdGrpId, a.cdGrpNm from BaseCode a group by a.id.cdGrpId, a.cdGrpNm",
            Sort.by("a.id.cdGrpId")).list()
    }
}

@ApplicationScoped
class BaseCodeEgenRepository : PanacheRepositoryBase<BaseCodeEgen, BaseCodeEgenId>

@ApplicationScoped
class BaseAttcRepository : PanacheRepositoryBase<BaseAttc, String>