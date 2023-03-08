package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.BaseCode
import org.sbas.entities.BaseCodeId
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BaseCodeRepository : PanacheRepositoryBase<BaseCode, BaseCodeId>