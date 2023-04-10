package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.talk.*
import org.sbas.responses.messages.TalkRoomResponse
import java.lang.NullPointerException
import java.math.BigDecimal
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TalkUserRepository : PanacheRepositoryBase<TalkUser, TalkUserId>

@ApplicationScoped
class TalkMsgRepository : PanacheRepositoryBase<TalkMsg, TalkMsgId> {

    fun findChatDetail(tkrmId: String) = find("select tm from TalkMsg tm where tm.id.tkrmId = '$tkrmId' order by tm.id.msgSeq").list()

    fun findRecentlyMsg(tkrmId: String?) = find("SELECT tm FROM TalkMsg tm WHERE tm.id.tkrmId = '$tkrmId' ORDER BY tm.id.msgSeq DESC").firstResult()

    fun findRecentSeq(tkrmId: String?) = find("select tm from TalkMsg tm where tm.id.tkrmId = '$tkrmId' order by tm.id.msgSeq desc limit 1").firstResult()

}

@ApplicationScoped
class TalkRoomRepository : PanacheRepositoryBase<TalkRoom, TalkRoomId> {

    fun findMyRooms(userId: String) = find("select tr from TalkRoom tr join TalkUser tu on tr.id.tkrmId = tu.id.tkrmId and tu.id.userId = '$userId'").list()

    fun findTalkRoomResponse(userId: String): List<TalkRoomResponse> {
        val resultList = ArrayList<TalkRoomResponse>()
        val talkRooms = findMyRooms(userId)
        val talkMsgRepository = TalkMsgRepository()

        talkRooms.forEach {
            val talkMsg = talkMsgRepository.findRecentlyMsg(it.id?.tkrmId)
            if (talkMsg != null) {
                resultList.add(TalkRoomResponse(it.id?.tkrmId, it.tkrmNm, talkMsg.msg, talkMsg.rgstDttm))
            } else {
                resultList.add(TalkRoomResponse(it.id?.tkrmId, it.tkrmNm, null, it.rgstDttm))
            }
        }

        return resultList
    }

    fun findTalkRoomByTkrmId(tkrmId: String) = find("select tr from TalkRoom tr where tr.id.tkrmId = '$tkrmId'")

    fun findTalkRoomResponseByTkrmId(tkrmId: String): TalkRoomResponse {
        val talkRoom = findTalkRoomByTkrmId(tkrmId).firstResult()
        val talkMsgRepository = TalkMsgRepository()
        val talkMsg = talkMsgRepository.findRecentlyMsg(tkrmId)

        if(talkRoom == null) throw NullPointerException("채팅방이 없습니다.")

        return if(talkMsg != null){
            TalkRoomResponse(tkrmId, talkRoom.tkrmNm, talkMsg.msg, talkMsg.rgstDttm)
        }else {
            TalkRoomResponse(tkrmId, talkRoom.tkrmNm, null, talkRoom.rgstDttm)
        }
    }
}