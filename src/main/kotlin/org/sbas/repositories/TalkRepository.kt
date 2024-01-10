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

    fun persistTalkUsers(regGroupTalkRoomDto: RegGroupTalkRoomDto, talkRoom: TalkRoom) {
        val regTalkUserIdSelf = TalkUserId(tkrmId = talkRoom.tkrmId, userId = regGroupTalkRoomDto.id)
        val regTalkUserSelf = TalkUser(
            id = regTalkUserIdSelf,
            hostYn = "Y",
            joinDt = talkRoom.cretDt,
            joinTm = talkRoom.cretTm,
            wtdrDt = null,
            wtdrTm = null
        )
        persist(regTalkUserSelf)

        regGroupTalkRoomDto.userIds?.map {
            val talkRoomId = TalkUserId(tkrmId = talkRoom.tkrmId, userId = it)
            val regTalkUser = TalkUser(
                id = talkRoomId,
                hostYn = "N",
                joinDt = talkRoom.cretDt,
                joinTm = talkRoom.cretTm,
                wtdrDt = null,
                wtdrTm = null
            )

            persist(regTalkUser)
        }
    }

    fun countByTkrmId(tkrmId: String): Int {
        return count("where id.tkrmId = '$tkrmId'").toInt()
    }

}

@ApplicationScoped
class TalkMsgRepository : PanacheRepositoryBase<TalkMsg, TalkMsgId> {
    @Transactional
    fun findChatDetail(tkrmId: String): List<TalkMsgDto> {
        val query = "select new org.sbas.dtos.TalkMsgDto(tm.id.tkrmId, tm.id.msgSeq, tm.id.histSeq, " +
            "tm.histCd, tm.msg, tm.attcId, " +
            "iu.instNm || ' / ' || iu.userNm, " +
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
            id = TalkMsgId(tkrmId, (recentMsgSeq?.id?.msgSeq ?: 0) + 1, 1),
            histCd = "1",
            msg = message,
            attcId = null,
            rgstUserId = userId,
            rgstDttm = Instant.now(),
            updtUserId = userId,
            updtDttm = Instant.now())
        runBlocking {
            persist(insertObject)
        }
        return insertObject
    }

    @Transactional
    fun insertFile(msg: String?, file: String?, tkrmId: String, userId: String): TalkMsg {
        val recentMsgSeq = findRecentlyMsg(tkrmId)
        val insertFile = TalkMsg(
            id = TalkMsgId(tkrmId, (recentMsgSeq?.id?.msgSeq ?: 0) + 1, 1),
            histCd = "1",
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
    fun findRecentlyMsg(tkrmId: String) = find("select tm from TalkMsg tm where tm.id.tkrmId = '$tkrmId' order by tm.id.msgSeq desc").firstResult()

    fun findRecentSeq(tkrmId: String?) = find("select tm from TalkMsg tm where tm.id.tkrmId = '$tkrmId' order by tm.id.msgSeq desc limit 1").firstResult()

}

@ApplicationScoped
class TalkRoomRepository : PanacheRepositoryBase<TalkRoom, String> {

    @Inject
    private lateinit var talkMsgRepository: TalkMsgRepository

    @Inject
    private lateinit var talkUserRepository: TalkUserRepository

    @Inject
    private lateinit var infoUserRepository: InfoUserRepository

    fun findMyRooms(userId: String): List<TalkRoom> {
        return find("select tr from TalkRoom tr join TalkUser tu on tr.tkrmId = tu.id.tkrmId and tu.id.userId = '$userId'").list()
    }

    @Transactional
    fun findTalkRoomResponse(userId: String): List<TalkRoomResponse> {
        val resultList = mutableListOf<TalkRoomResponse>()
        val talkRooms = findMyRooms(userId)

        runBlocking {
            talkRooms.forEach {
                val talkMsg = talkMsgRepository.findRecentlyMsg(it.tkrmId!!)
                if (talkMsg != null) {
                    resultList.add(TalkRoomResponse(it.tkrmId, it.tkrmNm, talkMsg.msg, talkMsg.rgstDttm))
                } else {
                    resultList.add(TalkRoomResponse(it.tkrmId, it.tkrmNm, null, it.rgstDttm))
                }
            }
        }

        return resultList
    }

    fun changePersonalTkrmNm(list: List<TalkRoomResponse>, userId: String) : List<TalkRoomResponse> {
        list.map {
            val count = talkUserRepository.countByTkrmId(it.tkrmId!!)
            if(count == 2 && it.tkrmNm == "") {
                val otherUserId = talkUserRepository.findOtherUsersByTkrmId(it.tkrmId!!, userId)[0].id?.userId!!
                val otherUserNm = infoUserRepository.findByUserId(otherUserId)!!.userNm
                it.tkrmNm = otherUserNm
            }
        }
        return list
    }

    fun findTalkRoomByTkrmId(tkrmId: String): TalkRoom? {
        return find("select tr from TalkRoom tr where tr.tkrmId = '$tkrmId'").firstResult()
    }

    @Transactional
    fun findTalkRoomResponseByTkrmId(tkrmId: String): TalkRoomResponse? {
        val talkRoom = findTalkRoomByTkrmId(tkrmId)
        return talkRoom?.let {
            val talkMsg = runBlocking { talkMsgRepository.findRecentlyMsg(tkrmId) }
            if (talkMsg != null) {
                TalkRoomResponse(tkrmId, it.tkrmNm, talkMsg.msg, talkMsg.rgstDttm)
            } else {
                TalkRoomResponse(tkrmId, it.tkrmNm, null, it.rgstDttm)
            }
        }
    }

    fun findNextId(): String {
        return find("select max(cast(tkrmId as integer)) + 1 from TalkRoom").firstResult().toString()
    }
}
