package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import org.sbas.entities.info.InfoPt
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class InfoPtRepository : PanacheRepository<InfoPt> {
    
}