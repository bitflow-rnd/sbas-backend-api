package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheQuery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import org.sbas.entities.info.InfoUser
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserInfoRepository : PanacheRepository<InfoUser> {

    fun findByUserId(userId: String) = find("id", userId).firstResult()

    fun deleteByUser(infoUser: InfoUser) = delete(infoUser)

    fun findLike(searchData: String): List<InfoUser> = find("select u from InfoUser u where u.id like '%$searchData%' or u.userNm like '%$searchData%' or u.btDt like '%$searchData%'").list()

    fun findId(infoUser: InfoUser): InfoUser? = find("select u  from InfoUser u where u.userNm = '${infoUser.userNm}' and u.telno = '${infoUser.telno}'").firstResult()

    fun existByUserId(userId: String): Boolean {
        return count("user_id = ?1", userId) == 1L
    }

    fun existByTelNo(telno: String): Boolean {
        return count("telno = ?1", telno) == 1L
    }

}