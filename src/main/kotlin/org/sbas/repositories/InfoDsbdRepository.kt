package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped
import org.sbas.entities.info.InfoDsbd

@ApplicationScoped
class InfoDsbdRepository : PanacheRepositoryBase<InfoDsbd, Long> {
}