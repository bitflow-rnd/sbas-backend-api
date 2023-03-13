package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.base.BaseCodeEgen
import org.sbas.entities.base.BaseCodeEgenId
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BaseCodeEgenRepository : PanacheRepositoryBase<BaseCodeEgen, BaseCodeEgenId>