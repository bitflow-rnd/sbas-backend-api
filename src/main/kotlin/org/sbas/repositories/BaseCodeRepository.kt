package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeId
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BaseCodeRepository : PanacheRepositoryBase<BaseCode, BaseCodeId> {

    fun findBaseCodeByCdGrpId(cdGrpId: String): List<BaseCode> {
        return find("cd_grp_id = ?1", cdGrpId).list()
    }
}