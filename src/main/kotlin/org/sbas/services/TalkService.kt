package org.sbas.services

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException
import org.sbas.constants.SbasConst
import org.sbas.dtos.InviteUserDto
import org.sbas.dtos.RegGroupTalkRoomDto
import org.sbas.dtos.RegTalkRoomDto
import org.sbas.dtos.TalkMsgDto
import org.sbas.entities.talk.TalkRoom
import org.sbas.entities.talk.TalkUser
import org.sbas.entities.talk.TalkUserId
import org.sbas.repositories.InfoUserRepository
import org.sbas.repositories.TalkMsgRepository
import org.sbas.repositories.TalkRoomRepository
import org.sbas.repositories.TalkUserRepository
import org.sbas.responses.CommonResponse
import org.sbas.responses.messages.TalkRoomResponse
import org.sbas.utils.StringUtils

@ApplicationScoped
class TalkService {

  @Inject
  private lateinit var talkUserRepository: TalkUserRepository

  @Inject
  private lateinit var talkMsgRepository: TalkMsgRepository

  @Inject
  private lateinit var talkRoomRepository: TalkRoomRepository

  @Inject
  private lateinit var infoUserRepository: InfoUserRepository

  @Transactional
  fun regPersonalChatRoom(regRequest: RegTalkRoomDto): CommonResponse<*> {
    val hasTalkRoom = talkUserRepository.findTkrmIdByUserId(regRequest)
    if (hasTalkRoom != null) {
      val talkRoom = talkRoomRepository.findTalkRoomByTkrmId(hasTalkRoom)
      val result = TalkRoomResponse(
        tkrmId = talkRoom?.tkrmId!!,
        tkrmNm = talkRoomRepository.changePersonalTkrmNm(talkRoom, regRequest.userId),
      )
      return CommonResponse(SbasConst.ResCode.FAIL, "해당 유저와의 대화방이 있습니다.", result)
    }

    val tkrmId = talkRoomRepository.findNextId()
    val regTalkRoom = regRequest.toEntity(tkrmId)

    talkRoomRepository.persist(regTalkRoom)

    val regTalkUserIdSelf = TalkUserId(tkrmId = tkrmId, userId = regRequest.id)
    val regTalkUserId = TalkUserId(tkrmId = tkrmId, userId = regRequest.userId)

    val invitedUserNm = infoUserRepository.findInfoUserById(regRequest.userId).userNm
    val myUserNm = infoUserRepository.findInfoUserById(regRequest.id).userNm

    val regTalkUserSelf = TalkUser(
      id = regTalkUserIdSelf,
      tkrmNm = invitedUserNm,
      hostYn = "Y",
      joinDt = regTalkRoom.cretDt,
      joinTm = regTalkRoom.cretTm,
      wtdrDt = null,
      wtdrTm = null
    )
    val regTalkUser = TalkUser(
      id = regTalkUserId,
      tkrmNm = myUserNm,
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
    val regTalkRoom = regRequest.toEntity(tkrmId)

    if (regRequest.tkrmNm.isNullOrEmpty()) {
      val tkrmNmForCreator = regRequest.userNmList.joinToString(", ")  // 초대된 유저들의 이름을 모두 합침
      talkUserRepository.persistTalkUsers(regRequest.id, regTalkRoom, tkrmNmForCreator, "Y")

      // 초대된 유저들의 tkrmNm 설정
      regRequest.userIdList.forEachIndexed { index, userId ->
        val otherUsers = listOf(regRequest.userNm) + regRequest.userNmList.filterIndexed { idx, _ -> idx != index }  // 만든 사람과 다른 유저들만 포함
        val tkrmNmForUser = otherUsers.joinToString(", ")
        talkUserRepository.persistTalkUsers(userId, regTalkRoom, tkrmNmForUser, "N")
      }
    } else { // 그룹 대화방 이름이 있을 경우
      talkUserRepository.persistTalkUsers(regRequest.id, regTalkRoom, regRequest.tkrmNm, "Y")

      // 초대된 유저들의 tkrmNm 설정
      regRequest.userIdList.forEach { userId ->
        talkUserRepository.persistTalkUsers(userId, regTalkRoom, regRequest.tkrmNm, "N")
      }
    }

    talkRoomRepository.persist(regTalkRoom)

    return CommonResponse("단체 채팅방을 만들었습니다.")
  }

  fun getMyChats(userId: String): CommonResponse<List<TalkRoomResponse>> {
    val findChatRooms = talkRoomRepository.findTalkRoomResponse(userId)

    val result = talkRoomRepository.changeTalkRoomListByPersonalTkrmNm(findChatRooms, userId)

    if (result.isEmpty()) throw NotFoundException("대화중인 채팅방이 없습니다.")

    return CommonResponse(result)
  }

  fun getMyChat(tkrmId: String): CommonResponse<List<TalkMsgDto>> {
    val findChatDetail = talkMsgRepository.findChatDetail(tkrmId)
    return CommonResponse(findChatDetail)
  }

  @Transactional
  fun inviteUser(inviteUserDto: InviteUserDto): CommonResponse<String> {
    val talkUserId = TalkUserId(tkrmId = inviteUserDto.tkrmId, userId = inviteUserDto.userId)
    val tkrmNm = talkUserRepository.findMainTkrmNmByTkrmId(inviteUserDto.tkrmId)

    val insertTalkUser = TalkUser(
      id = talkUserId,
      tkrmNm = tkrmNm,
      hostYn = "N",
      joinDt = StringUtils.getYyyyMmDd(),
      joinTm = StringUtils.getHhMmSs(),
    )

    talkUserRepository.persist(insertTalkUser)

    return CommonResponse("대화방에 초대하였습니다.")
  }

  @Transactional
  fun getAllChats(): CommonResponse<List<TalkRoom>> {
    return CommonResponse(talkRoomRepository.findAll().list())
  }
}