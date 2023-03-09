package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeId
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BaseCodeRepository : PanacheRepositoryBase<BaseCode, BaseCodeId>