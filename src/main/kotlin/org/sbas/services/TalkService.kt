package org.sbas.services

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException
import org.sbas.constants.SbasConst
import org.sbas.dtos.RegGroupTalkRoomDto
import org.sbas.dtos.RegTalkRoomDto
import org.sbas.entities.talk.*
import org.sbas.repositories.TalkMsgRepository
import org.sbas.repositories.TalkRoomRepository
import org.sbas.repositories.TalkUserRepository
import org.sbas.responses.CommonResponse
import org.sbas.responses.messages.TalkRoomResponse

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
            val result = TalkRoomResponse(
                tkrmId = talkRoom?.tkrmId,
                tkrmNm = talkRoomRepository.changePersonalTkrmNm(talkRoom!!, regRequest.userId),
            )
            return CommonResponse(SbasConst.ResCode.FAIL, "해당 유저와의 대화방이 있습니다.", result)
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

        val result = TalkRoomResponse(
            tkrmId = regTalkRoom.tkrmId,
            tkrmNm = talkRoomRepository.changePersonalTkrmNm(regTalkRoom, regRequest.userId),
        )

        return CommonResponse("채팅방을 만들었습니다.", result)
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

        val result = talkRoomRepository.changeTalkRoomListByPersonalTkrmNm(findChatRooms, userId)

        if(result.isEmpty()) throw NotFoundException("대화중인 채팅방이 없습니다.")

        return CommonResponse(result)
    }

    fun getMyChat(tkrmId: String) : CommonResponse<*>{
        val findChatDetail = talkMsgRepository.findChatDetail(tkrmId)
        return CommonResponse(findChatDetail)
    }

    @Transactional
    fun getAllChats(): CommonResponse<List<TalkRoom>> {
        return CommonResponse(talkRoomRepository.findAll().list())
    }
}