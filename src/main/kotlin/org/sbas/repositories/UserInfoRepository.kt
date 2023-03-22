package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheQuery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import org.sbas.entities.info.InfoUser
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserInfoRepository : PanacheRepository<InfoUser> {

    fun findByUserId(userId: String) = find("id", userId).firstResult()

    fun deleteByUser(infoUser: InfoUser) = delete(infoUser)

    fun findLike(searchData: String): List<InfoUser> = find("select u from InfoUser u where u.id like '%$searchData%' or u.userNm like '%$searchData%'").list()

}