package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import kotlinx.coroutines.runBlocking
import org.sbas.entities.talk.*
import org.sbas.responses.messages.TalkRoomResponse
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class TalkUserRepository : PanacheRepositoryBase<TalkUser, TalkUserId>{

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
            find("select tu tu where tu.id.tkrmId = '$tkrmId'").list()
        }
        return result
    }

}

@ApplicationScoped
class TalkMsgRepository : PanacheRepositoryBase<TalkMsg, TalkMsgId> {


    @Transactional
    fun findChatDetail(tkrmId: String) = find("select tm from TalkMsg tm where tm.id.tkrmId = '$tkrmId' order by tm.id.msgSeq").list()

    @Transactional
    fun insertMessage(message: String, tkrmId: String, userId: String): TalkMsg{
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
    fun findRecentlyMsg(tkrmId: String) = find("select tm from TalkMsg tm where tm.id.tkrmId = '$tkrmId' order by tm.id.msgSeq desc").firstResult()

    fun findRecentSeq(tkrmId: String?) = find("select tm from TalkMsg tm where tm.id.tkrmId = '$tkrmId' order by tm.id.msgSeq desc limit 1").firstResult()

}

@ApplicationScoped
class TalkRoomRepository : PanacheRepositoryBase<TalkRoom, TalkRoomId> {

    @Inject
    private lateinit var talkMsgRepository: TalkMsgRepository

    fun findMyRooms(userId: String): List<TalkRoom> {
        return find("select tr from TalkRoom tr join TalkUser tu on tr.id.tkrmId = tu.id.tkrmId and tu.id.userId = '$userId'").list()
    }

    @Transactional
    fun findTalkRoomResponse(userId: String): List<TalkRoomResponse> {
        val resultList = mutableListOf<TalkRoomResponse>()
        val talkRooms = findMyRooms(userId)

        runBlocking {
            talkRooms.forEach {
                val talkMsg = talkMsgRepository.findRecentlyMsg(it.id?.tkrmId!!)
                if (talkMsg != null) {
                    resultList.add(TalkRoomResponse(it.id?.tkrmId, it.tkrmNm, talkMsg.msg, talkMsg.rgstDttm))
                } else {
                    resultList.add(TalkRoomResponse(it.id?.tkrmId, it.tkrmNm, null, it.rgstDttm))
                }
            }
        }

        return resultList
    }
    fun findTalkRoomByTkrmId(tkrmId: String): TalkRoom? {
        return find("select tr from TalkRoom tr where tr.id.tkrmId = '$tkrmId'").firstResult()
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
}
