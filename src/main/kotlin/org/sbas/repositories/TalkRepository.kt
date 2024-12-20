package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import kotlinx.coroutines.runBlocking
import org.jboss.logging.Logger
import org.sbas.dtos.RegGroupTalkRoomDto
import org.sbas.dtos.RegTalkRoomDto
import org.sbas.dtos.TalkMsgDto
import org.sbas.entities.talk.*
import org.sbas.responses.messages.TalkRoomResponse
import java.time.Instant

@ApplicationScoped
class TalkUserRepository : PanacheRepositoryBase<TalkUser, TalkUserId> {

  @Transactional
  fun deleteTkrmByUserId(tkrmId: String, userId: String) {
    runBlocking {
      val deleteId = TalkUserId(tkrmId, userId)
      deleteById(deleteId)
    }
  }

  @Transactional
  fun findUsersByTkrmId(tkrmId: String): List<TalkUser> {
    val result = runBlocking {
      find("select tu from TalkUser tu where tu.id.tkrmId = '$tkrmId'").list()
    }
    return result
  }

  @Transactional
  fun findOtherUsersByTkrmId(tkrmId: String, userId: String): List<TalkUser> {
    val result = runBlocking {
      find("select tu from TalkUser tu where tu.id.tkrmId = '$tkrmId' and tu.id.userId != '$userId'").list()
    }
    return result
  }

  /**
   * 개인톡방 tkrmId 찾기
   */
  fun findTkrmIdByUserId(regTalkRoomDto: RegTalkRoomDto): String? {
    val query = """
            select
                distinct t1
            from
                TalkUser t1
            inner join
                TalkUser t2
            on
                t1.id.tkrmId = t2.id.tkrmId and t1.id.userId != t2.id.userId
            where
                t1.id.userId in ('${regTalkRoomDto.id}', '${regTalkRoomDto.userId}')
                and t2.id.userId in ('${regTalkRoomDto.id}', '${regTalkRoomDto.userId}')
            and not exists (
                        select 1
                        from TalkUser t3
                        where
                            t3.id.tkrmId = t1.id.tkrmId
                            and t3.id.userId not in ('${regTalkRoomDto.id}', '${regTalkRoomDto.userId}')
            )
            """.trimIndent()

    return find(query).firstResult()?.id?.tkrmId
  }

  fun persistTalkUsers(userId: String, talkRoom: TalkRoom, tkrmNm: String, hostYn: String) {
    val talkUserId = TalkUserId(tkrmId = talkRoom.tkrmId, userId = userId)
    val talkUser = TalkUser(
      id = talkUserId,
      tkrmNm = tkrmNm,
      hostYn = hostYn,
      joinDt = talkRoom.cretDt,
      joinTm = talkRoom.cretTm,
      wtdrDt = null,
      wtdrTm = null
    )
    persist(talkUser)
  }

  fun findTalkRoomNameById(tkrmId: String, userId: String) : String {
    return findById(TalkUserId(tkrmId, userId))?.tkrmNm ?: ""
  }

  fun findMainTkrmNmByTkrmId(tkrmId: String): String? {
    return find("select tr from TalkUser tr where tr.tkrmId = '${tkrmId}' and host_yn = 'Y'").firstResult()?.tkrmNm
  }

}

@ApplicationScoped
class TalkMsgRepository : PanacheRepositoryBase<TalkMsg, TalkMsgId> {
  @Transactional
  fun findChatDetail(tkrmId: String): List<TalkMsgDto> {
    val query = "select new org.sbas.dtos.TalkMsgDto(tm.id.tkrmId, tm.id.msgSeq, " +
      "tm.msg, tm.attcId, " +
      "iu.userNm, iu.instNm, tm.rgstUserId, " +
      "tm.rgstDttm, tm.updtUserId, tm.updtDttm) " +
      "from TalkMsg tm " +
      "inner join InfoUser iu on iu.id = tm.rgstUserId " +
      "where tm.id.tkrmId = '$tkrmId' order by tm.id.msgSeq "

    return getEntityManager().createQuery(query, TalkMsgDto::class.java).resultList
    //        return find("select tm from TalkMsg tm where tm.id.tkrmId = '$tkrmId' order by tm.id.msgSeq").list()
  }

  @Transactional
  fun insertMessage(message: String, tkrmId: String, userId: String): TalkMsg {
    val recentMsgSeq = findRecentlyMsg(tkrmId)
    val insertObject = TalkMsg(
      id = TalkMsgId(tkrmId, (recentMsgSeq?.id?.msgSeq ?: 0) + 1),
      msg = message,
      attcId = null,
      rgstUserId = userId,
      rgstDttm = Instant.now(),
      updtUserId = userId,
      updtDttm = Instant.now()
    )
    runBlocking {
      persist(insertObject)
    }
    return insertObject
  }

  @Transactional
  fun insertFile(msg: String?, file: String?, tkrmId: String, userId: String): TalkMsg {
    val recentMsgSeq = findRecentlyMsg(tkrmId)
    val insertFile = TalkMsg(
      id = TalkMsgId(tkrmId, (recentMsgSeq?.id?.msgSeq ?: 0) + 1),
      msg = msg ?: "",
      attcId = file,
      rgstUserId = userId,
      rgstDttm = Instant.now(),
      updtUserId = userId,
      updtDttm = Instant.now()
    )
    runBlocking {
      persist(insertFile)
    }
    return insertFile
  }

  @Transactional
  fun findRecentlyMsg(tkrmId: String) =
    find("select tm from TalkMsg tm where tm.id.tkrmId = '$tkrmId' order by tm.id.msgSeq desc").firstResult()
}

@ApplicationScoped
class TalkRoomRepository : PanacheRepositoryBase<TalkRoom, String> {

  @Inject
  private lateinit var talkMsgRepository: TalkMsgRepository

  @Inject
  private lateinit var talkUserRepository: TalkUserRepository

  @Inject
  private lateinit var infoUserRepository: InfoUserRepository

  @Inject
  private lateinit var log: Logger

  fun findMyRooms(userId: String): List<TalkRoom> {
    return find(
      "select tr from TalkRoom tr " +
        "join TalkUser tu on tr.tkrmId = tu.id.tkrmId and tu.id.userId = '$userId' " +
        "order by tr.rgstDttm desc"
    ).list()
  }

  @Transactional
  fun findTalkRoomResponse(userId: String): List<TalkRoomResponse> {
    val resultList = mutableListOf<TalkRoomResponse>()
    val talkRooms = findMyRooms(userId)

    runBlocking {
      talkRooms.forEach { talkRoom ->
        val tkrmNm = talkUserRepository.findTalkRoomNameById(talkRoom.tkrmId, userId)
        val talkMsg = talkMsgRepository.findRecentlyMsg(talkRoom.tkrmId)
        if (talkMsg != null) {
          resultList.add(TalkRoomResponse(talkRoom.tkrmId, tkrmNm, talkMsg.msg, talkMsg.rgstDttm))
        } else {
          resultList.add(TalkRoomResponse(talkRoom.tkrmId, tkrmNm, null, talkRoom.rgstDttm))
        }
      }
    }

    // rgstDttm으로 최신 순으로 정렬
    return resultList.sortedByDescending { it.rgstDttm }
  }

  fun changeTalkRoomListByPersonalTkrmNm(list: List<TalkRoomResponse>, userId: String): List<TalkRoomResponse> {
    list.map {
      it.tkrmNm = talkUserRepository.findTalkRoomNameById(it.tkrmId, userId)
    }
    return list
  }

  fun changePersonalTkrmNm(talkRoom: TalkRoom, userId: String): String {
    val userNm = infoUserRepository.findByUserId(userId)?.userNm
    return userNm ?: ""
  }

  fun findTalkRoomByTkrmId(tkrmId: String): TalkRoom? {
    return find("select tr from TalkRoom tr where tr.tkrmId = '$tkrmId'").firstResult()
  }

  @Transactional
  fun findTalkRoomResponseByTkrmId(tkrmId: String, userId: String): TalkRoomResponse? {
    val talkRoom = findTalkRoomByTkrmId(tkrmId)
    return talkRoom?.let {
      val talkMsg = runBlocking { talkMsgRepository.findRecentlyMsg(tkrmId) }
      val tkrmNm = talkUserRepository.findTalkRoomNameById(tkrmId, userId)

      log.warn("userId : $userId")

      if (talkMsg != null) {
        TalkRoomResponse(tkrmId, tkrmNm, talkMsg.msg, talkMsg.rgstDttm)
      } else {
        TalkRoomResponse(tkrmId, tkrmNm, null, it.rgstDttm)
      }
    }
  }

  fun findNextId(): String {
    return find("select max(cast(tkrmId as integer)) + 1 from TalkRoom").firstResult().toString()
  }
}
