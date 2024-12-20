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
import org.sbas.constants.enums.UserStatCd
import org.sbas.dtos.info.*
import org.sbas.entities.info.InfoPt
import org.sbas.entities.info.InfoUser
import org.sbas.entities.info.UserActivityHistory
import org.sbas.entities.info.UserFcmToken
import org.sbas.parameters.FindIdRequest
import org.sbas.parameters.InitPwRequest

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

  fun findInfoUserList(param: InfoUserSearchParam): List<UserDetailResponse> {
    //      val (cond, offset) = conditionAndOffset(param)

    val query = jpql {
      selectNew<UserDetailResponse>(
        path(InfoUser::id),
        path(InfoUser::userNm),
        path(InfoUser::gndr),
        path(InfoUser::telno),
        path(InfoUser::jobCd),
        path(InfoUser::ocpCd),
        path(InfoUser::ptTypeCd),
        path(InfoUser::instTypeCd),
        path(InfoUser::instId),
        path(InfoUser::instNm),
        path(InfoUser::dutyDstr1Cd),
        function(String::class, "fn_get_cd_nm", stringLiteral("SIDO"), path(InfoUser::dutyDstr1Cd)),
        path(InfoUser::dutyDstr2Cd),
        function(String::class, "fn_get_dstr_cd2_nm", path(InfoUser::dutyDstr1Cd), path(InfoUser::dutyDstr2Cd)),
        path(InfoUser::btDt),
        path(InfoUser::authCd),
        path(InfoUser::attcId),
        path(InfoUser::userStatCd),
        stringLiteral("userStatCdNm"),
        path(InfoUser::rgstDttm),
        path(InfoUser::updtDttm),
        path(InfoUser::aprvDttm),
      ).from(
        entity(InfoUser::class)
      ).whereAnd(
        param.userNm?.run { path(InfoUser::userNm).like("%${this}%") },
        param.telno?.run { path(InfoUser::telno).like("%${this}%") },
        param.ptTypeCd?.run { path(InfoUser::ptTypeCd).`in`(this.split(",")) },
        param.instTypeCd?.run { path(InfoUser::instTypeCd).`in`(this.split(",")) },
        param.userStatCdStr?.run { path(InfoUser::userStatCd).`in`(this.split(",").map { UserStatCd.valueOf(it) }) },
        param.dstr1Cd?.run { path(InfoUser::dutyDstr1Cd).eq(param.dstr1Cd) },
        param.dstr2Cd?.run { path(InfoUser::dutyDstr2Cd).eq(param.dstr2Cd) },
      ).orderBy(
        path(InfoUser::updtDttm).desc()
      )
    }

    val offset = param.page?.run { this.minus(1).times(15) } ?: 0

    return entityManager.createQuery(query, context).setMaxResults(15).setFirstResult(offset).resultList
  }

  fun findInfoUsersFromUser(param: InfoUserSearchFromUserParam): List<InfoUserListDto> {
    val cond = conditionAndOffsetFromUser(param)

    var query = """
            select new org.sbas.dtos.info.InfoUserListDto(iu.id, iu.dutyDstr1Cd, fn_get_cd_nm('SIDO', iu.dutyDstr1Cd), iu.telno, iu.ptTypeCd,
             iu.instTypeCd, iu.instNm, iu.userNm, iu.jobCd, iu.authCd, iu.rgstDttm, iu.userStatCd, iu.rgstUserId, iu.instId, iu.ocpCd, iu.updtDttm, false)
              from InfoUser iu where iu.id != '${jwt.name}'
        """.trimIndent()

    if (param.myInstTypeCd != "ORGN0005") {
      query += " and iu.userStatCd != 'URST0001'"
    }
    query += " and iu.userStatCd != 'URST0003' and $cond order by iu.updtDttm desc"

    val userList = entityManager.createQuery(query, InfoUserListDto::class.java).resultList

    userList.forEach {
      it.isFavorite = cntcRepository.findInfoCntcByUserIdAndMbrId(jwt.name, it.id) != null
    }

    return userList
  }

  fun findContactedInfoUserListByUserId(userId: String): List<InfoUserListDto> {

    val query = """
            select new org.sbas.dtos.info.InfoUserListDto(iu.id, iu.dutyDstr1Cd, fn_get_cd_nm('SIDO', iu.dutyDstr1Cd), iu.telno, iu.ptTypeCd, 
            iu.instTypeCd, iu.instNm, iu.userNm, iu.jobCd, iu.authCd, iu.rgstDttm, iu.userStatCd, iu.rgstUserId, iu.instId, iu.ocpCd, iu.updtDttm, false)
        from InfoUser iu
        join InfoCntc ic on ic.id.mbrId = iu.id
        where ic.id.userId = '$userId'
        order by iu.updtDttm desc
            """

    val userList = entityManager.createQuery(query, InfoUserListDto::class.java).resultList

    userList.forEach {
      it.isFavorite = cntcRepository.findInfoCntcByUserIdAndMbrId(jwt.name, it.id) != null
    }

    return userList
  }

  private fun conditionAndOffset(param: InfoUserSearchParam): Pair<String, Int> {
    var cond = param.userNm?.run { " (iu.userNm like '%$this%' " } ?: " (1=1"
    cond += param.telno?.run { " or iu.telno like '%$this%') " } ?: ")"

    cond += param.ptTypeCd?.run {
      " and fn_like_any(iu.ptTypeCd, '{%${
        this.split(',').joinToString("%, %")
      }%}') = true "
    }
      ?: ""
    cond += param.instTypeCd?.run { " and iu.instTypeCd in ('${this.split(',').joinToString("', '")}') " } ?: ""
    cond += param.userStatCdStr?.run { " and iu.userStatCd in ('${this.split(',').joinToString("', '")}') " } ?: ""

    cond += param.dstr1Cd?.run { " and iu.dutyDstr1Cd = '$this' " } ?: ""
    cond += param.dstr2Cd?.run { " and iu.dutyDstr2Cd = '$this' " } ?: ""

    val offset = param.page?.run { this.minus(1).times(15) } ?: 0

    return Pair(cond, offset)
  }

  private fun conditionAndOffsetFromUser(param: InfoUserSearchFromUserParam): String {
    var cond = param.instTypeCd?.run { "iu.instTypeCd in ('${this.split(',').joinToString("', '")}') " } ?: "1=1 "

    cond += param.search?.run { "and (iu.userNm like '%$this%' or iu.telno like '%$this%' or iu.instNm like '%$this%') " }
      ?: ""

    cond += param.dstr1Cd?.run { " and iu.dutyDstr1Cd like '%$this%' " } ?: ""
    cond += param.dstr2Cd?.run { " and iu.dutyDstr2Cd like '%$this%' " } ?: ""

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

    if (param.myInstTypeCd != "ORGN0005") {
      query += " and iu.userStatCd != 'URST0001'"
    }

    query += " and iu.userStatCd != 'URST0003' and $cond"

    return entityManager.createQuery(query).singleResult as Long
  }


  fun findId(findIdRequest: FindIdRequest): InfoUser? =
    find("select u from InfoUser u where u.userNm = '${findIdRequest.userNm}' and u.telno = '${findIdRequest.telno}'").firstResult()

  fun initPw(initPwRequest: InitPwRequest): InfoUser? =
    find("select u from InfoUser u where u.userNm = '${initPwRequest.userNm}' and u.telno = '${initPwRequest.telno}' and u.id = '${initPwRequest.id}'").firstResult()

  fun existByUserId(userId: String?): Boolean {
    return count("id = '$userId'") == 1L
  }

  fun existByTelNo(telno: String): Boolean {
    return count("telno = '$telno'") == 1L
  }

  fun findBdasUserByReqDstrCd(dstr1Cd: String?, dstr2Cd: String?): List<InfoUser> {
    val query = if (dstr2Cd != null) {
      "dutyDstr1Cd = '$dstr1Cd' and dutyDstr2Cd = '$dstr2Cd' and (jobCd = 'PMGR0002' OR jobCd like '병상승인%')"
    } else {
      "dutyDstr1Cd = '$dstr1Cd' and (jobCd = 'PMGR0002' OR jobCd like '병상승인%')"
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

  fun findAllInfoUser(): List<UserDetailResponse> {
    val query = jpql {
      selectNew<UserDetailResponse>(
        path(InfoUser::id),
        path(InfoUser::userNm),
        path(InfoUser::gndr),
        path(InfoUser::telno),
        path(InfoUser::jobCd),
        path(InfoUser::ocpCd),
        path(InfoUser::ptTypeCd),
        path(InfoUser::instTypeCd),
        path(InfoUser::instId),
        path(InfoUser::instNm),
        path(InfoUser::dutyDstr1Cd),
        function(String::class, "fn_get_cd_nm", stringLiteral("SIDO"), path(InfoUser::dutyDstr1Cd)),
        path(InfoUser::dutyDstr2Cd),
        function(String::class, "fn_get_dstr_cd2_nm", path(InfoUser::dutyDstr1Cd), path(InfoUser::dutyDstr2Cd)),
        path(InfoUser::btDt),
        path(InfoUser::authCd),
        path(InfoUser::attcId),
        path(InfoUser::userStatCd),
        stringLiteral("userStatCdNm"),
        path(InfoUser::rgstDttm),
        path(InfoUser::updtDttm),
        path(InfoUser::aprvDttm),
      ).from(
        entity(InfoUser::class)
      )
    }

    return entityManager.createQuery(query, context).resultList
  }

  fun findInfoUserById(userId: String): UserDetailResponse {
    val query = jpql {
      selectNew<UserDetailResponse>(
        path(InfoUser::id),
        path(InfoUser::userNm),
        path(InfoUser::gndr),
        path(InfoUser::telno),
        path(InfoUser::jobCd),
        path(InfoUser::ocpCd),
        path(InfoUser::ptTypeCd),
        path(InfoUser::instTypeCd),
        path(InfoUser::instId),
        path(InfoUser::instNm),
        path(InfoUser::dutyDstr1Cd),
        function(String::class, "fn_get_cd_nm", stringLiteral("SIDO"), path(InfoUser::dutyDstr1Cd)),
        path(InfoUser::dutyDstr2Cd),
        function(String::class, "fn_get_dstr_cd2_nm", path(InfoUser::dutyDstr1Cd), path(InfoUser::dutyDstr2Cd)),
        path(InfoUser::btDt),
        path(InfoUser::authCd),
        path(InfoUser::attcId),
        path(InfoUser::userStatCd),
        stringLiteral("userStatCdNm"),
        path(InfoUser::rgstDttm),
        path(InfoUser::updtDttm),
        path(InfoUser::aprvDttm),
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
    result.userStatCdNm = result.userStatCd?.name
    return result
  }
}

@ApplicationScoped
class UserFcmTokenRepository : PanacheRepositoryBase<UserFcmToken, Long> {

  fun findAllByUserId(userId: String): List<UserFcmToken> {
    return find("userId = '${userId}' and isValid = true").list()
  }

  fun findAllByUserIdList(list: List<String>): List<UserFcmToken> {
    return find("userId in ?1 and isValid = true", list).list()
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
        path(InfoPt::rrno1),
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