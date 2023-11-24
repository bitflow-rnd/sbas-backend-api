package org.sbas.repositories

import com.linecorp.kotlinjdsl.QueryFactory
import com.linecorp.kotlinjdsl.listQuery
import com.linecorp.kotlinjdsl.query.spec.ExpressionOrderSpec
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.querydsl.expression.column
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import kotlinx.coroutines.runBlocking
import org.sbas.dtos.bdas.BdasListSearchParam
import org.sbas.dtos.info.*
import org.sbas.entities.info.InfoUser
import org.sbas.entities.info.UserFcmToken
import org.sbas.parameters.PageRequest
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.transaction.Transactional

@ApplicationScoped
class InfoUserRepository : PanacheRepositoryBase<InfoUser, String> {

    @Inject
    private lateinit var entityManager: EntityManager

    @Inject
    private lateinit var queryFactory: QueryFactory

    @Transactional
    fun findByUserId(userId: String) = runBlocking { find("id", userId).firstResult() }

//    fun getMyUserDetail(userId: String) = find("from InfoUser where id = $userId").firstResult()

    fun findInfoUserList(param: InfoUserSearchParam): List<InfoUserListDto> {
        val (cond, offset) = conditionAndOffset(param)

        val query = "select new org.sbas.dtos.info.InfoUserListDto(iu.id, iu.dutyDstr1Cd, fn_get_cd_nm('SIDO', iu.dutyDstr1Cd), " +
                "iu.instTypeCd, iu.instNm, iu.userNm, iu.jobCd, iu.authCd, iu.rgstDttm, iu.userStatCd, iu.rgstUserId) " +
                "from InfoUser iu " +
                "where " + "$cond " + "order by iu.updtDttm desc"

        return entityManager.createQuery(query, InfoUserListDto::class.java).setMaxResults(15).setFirstResult(offset).resultList
    }

    private fun conditionAndOffset(param: InfoUserSearchParam): Pair<String, Int> {
        var cond = param.userNm?.run { " (iu.userNm like '%$this%' " } ?: " (1=1"
        cond += param.telno?.run { " or iu.telno like '%$this%') " } ?: ")"

        cond += param.ptTypeCd?.run { " and fn_like_any(iu.ptTypeCd, '{%${this.split(',').joinToString("%, %")}%}') = true " } ?: ""
        cond += param.instTypeCd?.run { " and iu.instTypeCd in ('${this.split(',').joinToString("', '")}') " } ?: ""
        cond += param.userStatCdStr?.run { " and iu.userStatCd in ('${this.split(',').joinToString("', '")}') " } ?: ""

        cond += param.dstr1Cd?.run { " and iu.dutyDstr1Cd like '%$this%' " } ?: ""
        cond += param.dstr2Cd?.run { " and iu.dutyDstr2Cd like '%$this%' " } ?: ""
        cond += param.instNm?.run { " and iu.instNm like '%$this%' " } ?: ""

        val offset = param.page?.run { this.minus(1).times(15) } ?: 0

        return Pair(cond, offset)
    }

    fun countInfoUserList(param: InfoUserSearchParam): Long {
        val (cond, _) = conditionAndOffset(param)

        val query = "select count(iu.id) from InfoUser iu where $cond"

        return entityManager.createQuery(query).singleResult as Long
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

    fun findBdasUserByReqDstrCd(dstrCd1: String?, dstrCd2: String?): List<InfoUser> {
        val query = if (dstrCd2 != null) {
            "duty_dstr_1_cd = '$dstrCd1' and duty_dstr_2_cd = '$dstrCd2' and (job_cd = 'PMGR0002' OR job_cd like '병상승인%')"
        } else {
            "duty_dstr_1_cd = '$dstrCd1' and (job_cd = 'PMGR0002' OR job_cd like '병상승인%')"
        }
        return find(query).list()
    }

    fun findMedicalInfoUser(hpId: String): MutableList<HospMedInfo> {
        val query = "select new org.sbas.dtos.info.HospMedInfo(iu.id, iu.dutyDstr1Cd, iu.ocpCd, " +
                "iu.userNm, iu.ptTypeCd, iu.jobCd, iu.authCd, iu.rgstDttm, iu.updtDttm, iu.userStatCd) " +
                "from InfoUser iu " +
                "join InfoBed ib on ib.hospId = iu.instId " +
                "where ib.hpId = '$hpId' "

        return getEntityManager().createQuery(query, HospMedInfo::class.java).resultList
    }

    fun findInfoUserDetail(userId: String?): List<UserDetailResponse> {
        val infoUserDetail = queryFactory.listQuery<UserDetailResponse> {
            selectMulti(
                col(InfoUser::id), col(InfoUser::userNm), col(InfoUser::gndr), col(InfoUser::telno),
                col(InfoUser::jobCd), col(InfoUser::ocpCd), col(InfoUser::ptTypeCd),
                col(InfoUser::instTypeCd), col(InfoUser::instId), col(InfoUser::instNm), col(InfoUser::dutyDstr1Cd),
                function("fn_get_cd_nm", String::class.java, literal("SIDO"), col(InfoUser::dutyDstr1Cd)),
                col(InfoUser::dutyDstr2Cd),
                function("fn_get_dstr_cd2_nm", String::class.java, col(InfoUser::dutyDstr1Cd), col(InfoUser::dutyDstr2Cd)),
                col(InfoUser::btDt), col(InfoUser::authCd), col(InfoUser::attcId), col(InfoUser::userStatCd), col(InfoUser::updtDttm),
            )
            from(entity(InfoUser::class))
            whereAnd(
                userId?.let { col(InfoUser::id).equal(userId) }
            )
        }

        return infoUserDetail
    }
}

@ApplicationScoped
class UserFcmTokenRepository : PanacheRepositoryBase<UserFcmToken, Long> {

    @Transactional
    fun findAllByUserId(userId: String): List<UserFcmToken> {
        return find("userId = '${userId}' and isValid = true").list()
    }
}
