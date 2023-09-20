package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.info.InfoNotice
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class NoticeRepository : PanacheRepositoryBase<InfoNotice, String>{

}