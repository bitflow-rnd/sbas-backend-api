package org.sbas.services

import org.eclipse.microprofile.jwt.JsonWebToken
import org.sbas.entities.talk.TalkMsg
import org.sbas.entities.talk.TalkUser
import org.sbas.repositories.TalkMsgRepository
import org.sbas.repositories.TalkUserRepository
import org.sbas.responses.CommonResponse
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.NotFoundException

@ApplicationScoped
class TalkService {

    @Inject
    private lateinit var talkUserRepository: TalkUserRepository

    @Inject
    private lateinit var talkMsgRepository: TalkMsgRepository

    fun getMyChats(jwt: JsonWebToken): CommonResponse<List<TalkUser>> {
        val findChatRooms = talkUserRepository.findChatsByUserId(jwt.name)

        if(findChatRooms.isEmpty()) throw NotFoundException("대화중인 채팅방이 없습니다.")

        return CommonResponse(findChatRooms)
    }

    fun getMyChat(tkrmId: String) : CommonResponse<List<TalkMsg>>{
        val findChatDetail = talkMsgRepository.findChatDetail(tkrmId)

        if(findChatDetail.isEmpty()) throw NotFoundException("채팅방에 저장된 메시지가 없습니다.")

        return CommonResponse(findChatDetail)
    }

}