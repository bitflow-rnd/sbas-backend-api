package org.sbas.repositories

import com.linecorp.kotlinjdsl.QueryFactory
import com.linecorp.kotlinjdsl.listQuery
import com.linecorp.kotlinjdsl.query.spec.ExpressionOrderSpec
import com.linecorp.kotlinjdsl.querydsl.expression.column
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import kotlinx.coroutines.runBlocking
import org.sbas.dtos.info.InfoUserListDto
import org.sbas.dtos.info.InfoUserSearchParam
import org.sbas.entities.info.InfoUser
import org.sbas.parameters.PageRequest
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class InfoUserRepository : PanacheRepositoryBase<InfoUser, String> {

    @Inject
    private lateinit var queryFactory: QueryFactory

    @Transactional
    fun findByUserId(userId: String) = runBlocking { find("id", userId).firstResult() }

//    fun getMyUserDetail(userId: String) = find("from InfoUser where id = $userId").firstResult()

    fun findInfoUserList(param: InfoUserSearchParam): List<InfoUserListDto> {
        //TODO 페이징 처리?
        val infoUserList: List<InfoUserListDto> = queryFactory.listQuery {
            select(listOf(column(InfoUser::id), column(InfoUser::dutyDstr1Cd), column(InfoUser::instTypeCd),
                column(InfoUser::instNm), column(InfoUser::userNm), column(InfoUser::jobCd), column(InfoUser::authCd),
                column(InfoUser::rgstDttm), column(InfoUser::userStatCd), column(InfoUser::rgstUserId)))
            from(entity(InfoUser::class))
            whereAnd(
                param.dstr1Cd?.run { column(InfoUser::dutyDstr1Cd).equal(this) },
                param.dstr2Cd?.run { column(InfoUser::dutyDstr2Cd).equal(this) },
                param.instTypeCd?.run { column(InfoUser::instTypeCd).`in`(this) },
                param.ptTypeCd?.run { column(InfoUser::ptTypeCd).`in`(this) },
                param.userStatCd?.run { column(InfoUser::userStatCd).equal(this) },
                param.userNm?.run { column(InfoUser::userNm).like("%$this%") },
                param.telno?.run { column(InfoUser::telno).like("%$this%") },
                param.instNm?.run { column(InfoUser::instNm).like("%$this%") },
            )
            orderBy(ExpressionOrderSpec(column(InfoUser::rgstDttm), ascending = false))
        }
        return infoUserList
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