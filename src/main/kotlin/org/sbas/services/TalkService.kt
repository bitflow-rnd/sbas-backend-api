package org.sbas.services

import org.sbas.repositories.TalkMsgRepository
import org.sbas.repositories.TalkRoomRepository
import org.sbas.repositories.TalkUserRepository
import org.sbas.responses.CommonResponse
import org.sbas.responses.messages.TalkRoomResponse
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException
import org.sbas.dtos.RegTalkRoomDto
import org.sbas.entities.talk.*

@ApplicationScoped
class TalkService {

    @Inject
    private lateinit var talkUserRepository: TalkUserRepository

    @Inject
    private lateinit var talkMsgRepository: TalkMsgRepository

    @Inject
    private lateinit var talkRoomRepository: TalkRoomRepository

    @Transactional
    fun regChatRoom(regRequest: RegTalkRoomDto): CommonResponse<String> {
        val tkrmId = talkRoomRepository.findAll().stream()
            .max(compareBy { it.tkrmId }).orElse(null).tkrmId ?: "0"
        val result = (tkrmId.toInt() + 1).toString()
        val regTalkRoom = regRequest.toEntity(result)

        talkRoomRepository.persist(regTalkRoom)

        val regTalkUserIdSelf = TalkUserId(tkrmId = result, userId = regRequest.id)
        val regTalkUserId = TalkUserId(tkrmId = result, userId = regRequest.userId)

        val regTalkUserSelf = TalkUser(
            id = regTalkUserIdSelf,
            hostYn = "Y",
            joinDt = regTalkRoom.cretDt,
            joinTm = regTalkRoom.cretTm,
            wtdrDt = null,
            wtdrTm = null
            )
        val regTalkUser = TalkUser(
            id = regTalkUserId,
            hostYn = "N",
            joinDt = regTalkRoom.cretDt,
            joinTm = regTalkRoom.cretTm,
            wtdrDt = null,
            wtdrTm = null
        )

        talkUserRepository.persist(regTalkUserSelf)
        talkUserRepository.persist(regTalkUser)

        return CommonResponse("채팅방을 만들었습니다.")
    }

    fun getMyChats(userId: String): CommonResponse<List<TalkRoomResponse>> {
        val findChatRooms = talkRoomRepository.findTalkRoomResponse(userId)

        if(findChatRooms.isEmpty()) throw NotFoundException("대화중인 채팅방이 없습니다.")

        return CommonResponse(findChatRooms)
    }

    fun getMyChat(tkrmId: String) : CommonResponse<*>{
        val findChatDetail = talkMsgRepository.findChatDetail(tkrmId)

        if(findChatDetail.isEmpty()) throw NotFoundException("채팅방에 저장된 메시지가 없습니다.")

        return CommonResponse(findChatDetail)
    }

//    fun getMyChatByTkrmId(tkrmId: String): TalkRoomResponse {
//        return talkRoomRepository.findTalkRoomResponseByTkrmId(tkrmId)
//    }

    @Transactional
    fun sendMsg(tkrmId: String, userId: String, detail: String){
        val recentMsg = talkMsgRepository.findRecentlyMsg(tkrmId)
        val addMsgId = TalkMsgId(tkrmId, recentMsg?.id?.msgSeq?.plus(1), recentMsg?.id?.histSeq)
        val addMsg = TalkMsg(addMsgId, recentMsg?.histCd, detail, null)
        addMsg.rgstUserId = userId
        addMsg.updtUserId = userId
        talkMsgRepository.persist(addMsg)
    }

    @Transactional
    fun getAllChats(): CommonResponse<List<TalkRoom>> {
        return CommonResponse(talkRoomRepository.findAll().list())
    }
}