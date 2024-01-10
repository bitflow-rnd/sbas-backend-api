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
import org.sbas.constants.SbasConst
import org.sbas.dtos.RegGroupTalkRoomDto
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
    fun regPersonalChatRoom(regRequest: RegTalkRoomDto): CommonResponse<*> {
        val hasTalkRoom = talkUserRepository.findTkrmIdByUserId(regRequest)
        if(hasTalkRoom != null) {
            val talkRoom = talkRoomRepository.findTalkRoomByTkrmId(hasTalkRoom)
            return CommonResponse(SbasConst.ResCode.FAIL, "해당 유저와의 대화방이 있습니다.", talkRoom)
        }

        val tkrmId = talkRoomRepository.findNextId()
        val regTalkRoom = regRequest.toEntity(tkrmId)

        talkRoomRepository.persist(regTalkRoom)

        val regTalkUserIdSelf = TalkUserId(tkrmId = tkrmId, userId = regRequest.id)
        val regTalkUserId = TalkUserId(tkrmId = tkrmId, userId = regRequest.userId)

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

        return CommonResponse("채팅방을 만들었습니다.", regTalkRoom)
    }

    @Transactional
    fun regGroupChatRoom(regRequest: RegGroupTalkRoomDto): CommonResponse<String> {
        val tkrmId = talkRoomRepository.findNextId()

        if(regRequest.tkrmNm == "" || regRequest.tkrmNm == null) {
            var tkrmNm = ""
            tkrmNm += regRequest.userIds?.joinToString(", ")

            if(tkrmNm == "") regRequest.tkrmNm = regRequest.id
            else regRequest.tkrmNm = tkrmNm
        }

        val regTalkRoom = regRequest.toEntity(tkrmId)

        talkRoomRepository.persist(regTalkRoom)

        talkUserRepository.persistTalkUsers(regRequest, regTalkRoom)

        return CommonResponse("단체 채팅방을 만들었습니다.")
    }

    fun getMyChats(userId: String): CommonResponse<List<TalkRoomResponse>> {
        val findChatRooms = talkRoomRepository.findTalkRoomResponse(userId)

        val result = talkRoomRepository.changePersonalTkrmNm(findChatRooms, userId)

        if(result.isEmpty()) throw NotFoundException("대화중인 채팅방이 없습니다.")

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