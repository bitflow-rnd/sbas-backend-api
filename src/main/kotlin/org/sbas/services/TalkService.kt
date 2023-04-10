package org.sbas.services

import org.eclipse.microprofile.jwt.JsonWebToken
import org.sbas.entities.talk.TalkMsg
import org.sbas.entities.talk.TalkMsgId
import org.sbas.entities.talk.TalkRoom
import org.sbas.entities.talk.TalkRoomId
import org.sbas.repositories.TalkMsgRepository
import org.sbas.repositories.TalkRoomRepository
import org.sbas.repositories.TalkUserRepository
import org.sbas.responses.CommonResponse
import org.sbas.responses.messages.TalkRoomResponse
import java.math.BigDecimal
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.NotFoundException

@ApplicationScoped
class TalkService {

    @Inject
    private lateinit var talkUserRepository: TalkUserRepository

    @Inject
    private lateinit var talkMsgRepository: TalkMsgRepository

    @Inject
    private lateinit var talkRoomRepository: TalkRoomRepository

    fun getMyChats(jwt: JsonWebToken): CommonResponse<List<TalkRoomResponse>> {
        val findChatRooms = talkRoomRepository.findTalkRoomResponse(jwt.name)

        if(findChatRooms.isEmpty()) throw NotFoundException("대화중인 채팅방이 없습니다.")

        return CommonResponse(findChatRooms)
    }

    fun getMyChat(tkrmId: String) : CommonResponse<List<TalkMsg>>{
        val findChatDetail = talkMsgRepository.findChatDetail(tkrmId)

        if(findChatDetail.isEmpty()) throw NotFoundException("채팅방에 저장된 메시지가 없습니다.")

        return CommonResponse(findChatDetail)
    }

    fun getMyChatByTkrmId(tkrmId: String): TalkRoomResponse {
        return talkRoomRepository.findTalkRoomResponseByTkrmId(tkrmId)
    }

    @Transactional
    fun sendMsg(tkrmId: String, userId: String, detail: String){
        val recentMsg = talkMsgRepository.findRecentlyMsg(tkrmId)
        val addMsgId = TalkMsgId(tkrmId, recentMsg?.id?.msgSeq?.plus(BigDecimal(1)), recentMsg?.id?.histSeq)
        val addMsg = TalkMsg(addMsgId, recentMsg?.histCd, detail, null)
        addMsg.rgstUserId = userId
        addMsg.updtUserId = userId
        talkMsgRepository.persist(addMsg)
    }

}