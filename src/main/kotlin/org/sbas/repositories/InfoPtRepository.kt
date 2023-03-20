package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.info.InfoPt
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class InfoPtRepository : PanacheRepositoryBase<InfoPt, String>