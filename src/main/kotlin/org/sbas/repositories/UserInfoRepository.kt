package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import org.sbas.entities.info.InfoUser
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserInfoRepository : PanacheRepository<InfoUser> {

    fun findByUserId(userId: String) = find("id", userId).firstResult()

}