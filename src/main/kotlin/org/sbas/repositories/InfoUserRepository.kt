package org.sbas.repositories

import com.linecorp.kotlinjdsl.QueryFactory
import com.linecorp.kotlinjdsl.QueryFactoryImpl
import com.linecorp.kotlinjdsl.listQuery
import com.linecorp.kotlinjdsl.query.creator.CriteriaQueryCreatorImpl
import com.linecorp.kotlinjdsl.query.creator.SubqueryCreatorImpl
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import kotlinx.coroutines.runBlocking
import org.sbas.dtos.InfoUserDto
import org.sbas.entities.info.InfoUser
import org.sbas.parameters.PageRequest
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.transaction.Transactional

@ApplicationScoped
class InfoUserRepository : PanacheRepositoryBase<InfoUser, String> {

    @Inject
    private lateinit var entityManager: EntityManager
    private lateinit var queryFactory: QueryFactory

    @PostConstruct
    fun initialize() {
        queryFactory = QueryFactoryImpl(
            criteriaQueryCreator = CriteriaQueryCreatorImpl(entityManager),
            subqueryCreator = SubqueryCreatorImpl()
        )
    }

    @Transactional
    fun findByUserId(userId: String) = runBlocking { find("id", userId).firstResult() }

//    fun getMyUserDetail(userId: String) = find("from InfoUser where id = $userId").firstResult()

    fun findInfoUserList(searchData: String): List<InfoUser> {
//        find("select u from InfoUser u where u.id like '%$searchData%' or u.userNm like '%$searchData%' or u.btDt like '%$searchData%'").project(InfoUserDto::class.java).list()
        val query = "select new org.sbas.dtos.InfoUserDto(iu.id, iu.dutyDstr1Cd, iu.instTypeCd, iu.instNm, " +
                "iu.userNm, iu.jobCd, iu.authCd, iu.rgstDttm, fn_get_cd_nm('URST', iu.statClas)) " +
                "from InfoUser iu " +
                "order by iu.rgstDttm desc "
        val infoUsers = queryFactory.listQuery<InfoUser> {
            select(entity(InfoUser::class))
            from(entity(InfoUser::class))
        }
        return infoUsers
    }

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