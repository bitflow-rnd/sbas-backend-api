package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import kotlinx.coroutines.runBlocking
import org.sbas.entities.info.InfoUser
import org.sbas.parameters.PageRequest
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class InfoUserRepository : PanacheRepositoryBase<InfoUser, String> {

    @Transactional
    fun findByUserId(userId: String) = runBlocking { find("id", userId).firstResult() }

    fun findLike(searchData: String): List<InfoUser> = find("select u from InfoUser u where u.id like '%$searchData%' or u.userNm like '%$searchData%' or u.btDt like '%$searchData%'").list()

    fun findId(infoUser: InfoUser): InfoUser? = find("select u from InfoUser u where u.userNm = '${infoUser.userNm}' and u.telno = '${infoUser.telno}'").firstResult()

    fun existByUserId(userId: String): Boolean {
        return count("user_id = '$userId'") == 1L
    }

    fun existByTelNo(telno: String): Boolean {
        return count("telno = '$telno'") == 1L
    }

    fun findAllUsers(pageRequest: PageRequest): List<InfoUser> {
        val page = pageRequest.page ?: 1
        val size = pageRequest.size ?: 10
        return find("order by id").page(page-1, size).list()
    }


}