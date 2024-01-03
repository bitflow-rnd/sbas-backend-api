package org.sbas.repositories

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.hibernate.extension.createQuery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import kotlinx.coroutines.runBlocking
import org.eclipse.microprofile.jwt.JsonWebToken
import org.sbas.dtos.info.*
import org.sbas.entities.info.InfoPt
import org.sbas.entities.info.InfoUser
import org.sbas.entities.info.UserActivityHistory
import org.sbas.entities.info.UserFcmToken
import org.sbas.parameters.PageRequest

@ApplicationScoped
class InfoUserRepository : PanacheRepositoryBase<InfoUser, String> {

    @Inject
    private lateinit var entityManager: EntityManager

    @Inject
    private lateinit var context: JpqlRenderContext

    @Inject
    private lateinit var jwt: JsonWebToken

    @Inject
    private lateinit var cntcRepository: InfoCntcRepository

    @Transactional
    fun findByUserId(userId: String) = runBlocking { find("id", userId).firstResult() }

//    fun getMyUserDetail(userId: String) = find("from InfoUser where id = $userId").firstResult()

    fun findInfoUserList(param: InfoUserSearchParam): List<InfoUserListDto> {
        val (cond, offset) = conditionAndOffset(param)

        val query = "select new org.sbas.dtos.info.InfoUserListDto(iu.id, iu.dutyDstr1Cd, fn_get_cd_nm('SIDO', iu.dutyDstr1Cd), iu.telno, " +
            "iu.instTypeCd, iu.instNm, iu.userNm, iu.jobCd, iu.authCd, iu.rgstDttm, iu.userStatCd, iu.rgstUserId, iu.instId, iu.ocpCd, false) " +
            "from InfoUser iu " +
            "where " + "$cond " + "order by iu.updtDttm desc"

        return entityManager.createQuery(query, InfoUserListDto::class.java).setMaxResults(15).setFirstResult(offset).resultList
    }

    fun findInfoUsersFromUser(param : InfoUserSearchFromUserParam): List<InfoUserListDto> {
        val cond = conditionAndOffsetFromUser(param)

        var query = """
            select new org.sbas.dtos.info.InfoUserListDto(iu.id, iu.dutyDstr1Cd, fn_get_cd_nm('SIDO', iu.dutyDstr1Cd), iu.telno,
             iu.instTypeCd, iu.instNm, iu.userNm, iu.jobCd, iu.authCd, iu.rgstDttm, iu.userStatCd, iu.rgstUserId, iu.instId, iu.ocpCd, false)
              from InfoUser iu where iu.id != '${jwt.name}'
        """.trimIndent()

        if(param.myInstTypeCd != "ORGN0005") {
            query += " and iu.userStatCd != 'URST0001'"
        }
        query += " and iu.userStatCd != 'URST0003' and $cond order by iu.updtDttm desc"

        val userList = entityManager.createQuery(query, InfoUserListDto::class.java).resultList

        userList.forEach{
            it.isFavorite = cntcRepository.findInfoCntcByUserIdAndMbrId(jwt.name, it.id) != null
        }

        return userList

    }

    fun findContactedInfoUserListByUserId(userId: String): List<InfoUserListDto> {

        val query = """
            select new org.sbas.dtos.info.InfoUserListDto(iu.id, iu.dutyDstr1Cd, fn_get_cd_nm('SIDO', iu.dutyDstr1Cd), iu.telno,
            iu.instTypeCd, iu.instNm, iu.userNm, iu.jobCd, iu.authCd, iu.rgstDttm, iu.userStatCd, iu.rgstUserId, iu.instId, iu.ocpCd, false)
        from InfoUser iu
        join InfoCntc ic on ic.id.mbrId = iu.id
        where ic.id.userId = '$userId'
        order by iu.updtDttm desc
            """

        val userList = entityManager.createQuery(query, InfoUserListDto::class.java).resultList

        userList.forEach{
            it.isFavorite = cntcRepository.findInfoCntcByUserIdAndMbrId(jwt.name, it.id) != null
        }

        return userList
    }

    private fun conditionAndOffset(param: InfoUserSearchParam): Pair<String, Int> {
        var cond = param.userNm?.run { " (iu.userNm like '%$this%' " } ?: " (1=1"
        cond += param.telno?.run { " or iu.telno like '%$this%') " } ?: ")"

        cond += param.ptTypeCd?.run { " and fn_like_any(iu.ptTypeCd, '{%${this.split(',').joinToString("%, %")}%}') = true " }
            ?: ""
        cond += param.instTypeCd?.run { " and iu.instTypeCd in ('${this.split(',').joinToString("', '")}') " } ?: ""
        cond += param.userStatCdStr?.run { " and iu.userStatCd in ('${this.split(',').joinToString("', '")}') " } ?: ""

        cond += param.dstr1Cd?.run { " and iu.dutyDstr1Cd = '$this' " } ?: ""
        cond += param.dstr2Cd?.run { " and iu.dutyDstr2Cd = '$this' " } ?: ""
        cond += param.instNm?.run { " and iu.instNm like '%$this%' " } ?: ""

        val offset = param.page?.run { this.minus(1).times(15) } ?: 0

        return Pair(cond, offset)
    }

    private fun conditionAndOffsetFromUser(param: InfoUserSearchFromUserParam): String {
        var cond = param.userNm?.run { " (iu.userNm like '%$this%' " } ?: " (1=1"
        cond += param.telno?.run { " and iu.telno like '%$this%') " } ?: ")"

        cond += param.ptTypeCd?.run { " and fn_like_any(iu.ptTypeCd, '{%${this.split(',').joinToString("%, %")}%}') = true " }
            ?: ""
        cond += param.instTypeCd?.run { " and iu.instTypeCd in ('${this.split(',').joinToString("', '")}') " } ?: ""

        cond += param.dstr1Cd?.run { " and iu.dutyDstr1Cd like '%$this%' " } ?: ""
        cond += param.dstr2Cd?.run { " and iu.dutyDstr2Cd like '%$this%' " } ?: ""
        cond += param.instNm?.run { " and iu.instNm like '%$this%' " } ?: ""

        return cond
    }

    fun countInfoUserList(param: InfoUserSearchParam): Long {
        val (cond, _) = conditionAndOffset(param)

        val query = "select count(iu.id) from InfoUser iu where $cond"

        return entityManager.createQuery(query).singleResult as Long
    }

    fun countInfoUsersFromUser(param: InfoUserSearchFromUserParam): Long {
        val cond = conditionAndOffsetFromUser(param)

        var query = "select count(iu.id) from InfoUser iu where iu.id != '${jwt.name}'"

        if(param.myInstTypeCd != "ORGN0005") {
            query += " and iu.userStatCd != 'URST0001'"
        }

        query += " and iu.userStatCd != 'URST0003' and $cond"

        return entityManager.createQuery(query).singleResult as Long
    }


    fun findId(infoUser: InfoUser): InfoUser? = find("select u from InfoUser u where u.userNm = '${infoUser.userNm}' and u.telno = '${infoUser.telno}'").firstResult()

    fun existByUserId(userId: String?): Boolean {
        return count("id = '$userId'") == 1L
    }

    fun existByTelNo(telno: String): Boolean {
        return count("telno = '$telno'") == 1L
    }

    fun findAllUsers(pageRequest: PageRequest): List<InfoUser> {
        val page = pageRequest.page ?: 1
        val size = pageRequest.size ?: 10
        return find("order by id").page(page - 1, size).list()
    }

    fun findBdasUserByReqDstrCd(dstrCd1: String?, dstrCd2: String?): List<InfoUser> {
        val query = if (dstrCd2 != null) {
            "dutyDstr1Cd = '$dstrCd1' and dutyDstr2Cd = '$dstrCd2' and (jobCd = 'PMGR0002' OR jobCd like '병상승인%')"
        } else {
            "dutyDstr1Cd = '$dstrCd1' and (jobCd = 'PMGR0002' OR jobCd like '병상승인%')"
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

    fun findInfoUserDetail(): List<UserDetailResponse> {
        val query = jpql {
            selectNew<UserDetailResponse>(
                path(InfoUser::id), path(InfoUser::userNm), path(InfoUser::gndr), path(InfoUser::telno),
                path(InfoUser::jobCd), path(InfoUser::ocpCd), path(InfoUser::ptTypeCd),
                path(InfoUser::instTypeCd), path(InfoUser::instId), path(InfoUser::instNm), path(InfoUser::dutyDstr1Cd),
                function(String::class, "fn_get_cd_nm", stringLiteral("SIDO"), path(InfoUser::dutyDstr1Cd)),
                path(InfoUser::dutyDstr2Cd),
                function(String::class, "fn_get_dstr_cd2_nm", path(InfoUser::dutyDstr1Cd), path(InfoUser::dutyDstr2Cd)),
                path(InfoUser::btDt), path(InfoUser::authCd), path(InfoUser::attcId), path(InfoUser::userStatCd), path(InfoUser::updtDttm),
            ).from(
                entity(InfoUser::class)
            )
        }

        return entityManager.createQuery(query, context).resultList
    }

    fun findInfoUserById(userId: String): UserDetailResponse {
        val query = jpql {
            selectNew<UserDetailResponse>(
                path(InfoUser::id), path(InfoUser::userNm), path(InfoUser::gndr), path(InfoUser::telno),
                path(InfoUser::jobCd), path(InfoUser::ocpCd), path(InfoUser::ptTypeCd),
                path(InfoUser::instTypeCd), path(InfoUser::instId), path(InfoUser::instNm), path(InfoUser::dutyDstr1Cd),
                function(String::class, "fn_get_cd_nm", stringLiteral("SIDO"), path(InfoUser::dutyDstr1Cd)),
                path(InfoUser::dutyDstr2Cd),
                function(String::class, "fn_get_dstr_cd2_nm", path(InfoUser::dutyDstr1Cd), path(InfoUser::dutyDstr2Cd)),
                path(InfoUser::btDt), path(InfoUser::authCd), path(InfoUser::attcId), path(InfoUser::userStatCd), path(InfoUser::updtDttm),
            ).from(
                entity(InfoUser::class)
            ).whereAnd(
                path(InfoUser::id).eq(userId)
            )
        }

        val infoUserDetail = entityManager.createQuery(query, context).resultList
        val isFavorite = cntcRepository.findInfoCntcByUserIdAndMbrId(jwt.name, userId) != null

        val result = infoUserDetail.first()
        result.isFavorite = isFavorite

        return result
    }
}

@ApplicationScoped
class UserFcmTokenRepository : PanacheRepositoryBase<UserFcmToken, Long> {

    fun findAllByUserId(userId: String): List<UserFcmToken> {
        return find("userId = '${userId}' and isValid = true").list()
    }

    fun findAllByUserIdList(list: List<String>): List<UserFcmToken>{
        return find("userId in (${list.joinToString("','", "'", "'")}) and isValid = true").list()
    }
}

@ApplicationScoped
class UserActivityHistoryRepository : PanacheRepositoryBase<UserActivityHistory, Int> {

    @Inject
    private lateinit var entityManager: EntityManager

    @Inject
    private lateinit var context: JpqlRenderContext

    fun save(userActivityHistory: UserActivityHistory): UserActivityHistory {
        persist(userActivityHistory)
        return userActivityHistory
    }

    fun findAllByUserId(userId: String): List<UserActivityHistoryResponse> {
        val query = jpql {
            selectNew<UserActivityHistoryResponse>(
                path(UserActivityHistory::id),
                path(UserActivityHistory::userId),
                path(UserActivityHistory::ptId),
                path(InfoPt::ptNm), path(InfoPt::gndr),
                function(String::class, "fn_get_age", path(InfoPt::rrno1), path(InfoPt::rrno2)),
                path(InfoPt::dstr1Cd),
                function(String::class, "fn_get_cd_nm", stringLiteral("SIDO"), path(InfoPt::dstr1Cd)),
                path(InfoPt::dstr2Cd),
                function(String::class, "fn_get_dstr_cd2_nm", path(InfoPt::dstr1Cd), path(InfoPt::dstr2Cd)),
                path(UserActivityHistory::activityDetail),
                path(UserActivityHistory::rgstDttm),
            ).from(
                entity(UserActivityHistory::class),
                join(InfoPt::class).on(path(UserActivityHistory::ptId).eq(path(InfoPt::ptId))),
            ).whereAnd(
                path(UserActivityHistory::userId).eq(userId),
            )
        }

        return entityManager.createQuery(query, context).resultList
    }
}