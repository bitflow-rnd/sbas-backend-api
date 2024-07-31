package org.sbas.utils

import com.google.gson.Gson
import io.vertx.core.json.JsonObject
import jakarta.inject.Inject
import jakarta.websocket.OnClose
import jakarta.websocket.OnMessage
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.eclipse.microprofile.context.ManagedExecutor
import org.jboss.logging.Logger
import org.json.JSONArray
import org.sbas.dtos.AttcIdResponse
import org.sbas.dtos.TalkMsgDto
import org.sbas.entities.talk.TalkMsg
import org.sbas.entities.talk.TalkUser
import org.sbas.repositories.TalkMsgRepository
import org.sbas.repositories.TalkRoomRepository
import org.sbas.repositories.TalkUserRepository
import org.sbas.responses.messages.TalkRoomResponse
import org.sbas.services.FirebaseService


@ServerEndpoint("/chat-rooms/room/{tkrmId}")
class TalkRoomMod {

    companion object {
        private val chatSockets = mutableMapOf<String, TalkRoomMod>() // WebSocket 연결을 관리할 Map
        private lateinit var talkMsg: MutableList<TalkMsgDto>
    }

    private lateinit var session: Session // WebSocket 세션
    private lateinit var tkrmId: String // 채팅방 ID
    private lateinit var userId: String // 접속 사용자 ID

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var managedExecutor: ManagedExecutor

    @Inject
    private lateinit var firebaseService: FirebaseService

    @Inject
    lateinit var talkMsgRepository: TalkMsgRepository

    @Inject
    lateinit var talkRoomRepository: TalkRoomRepository

    @Inject
    lateinit var talkUserRepository: TalkUserRepository

    @OnOpen
    fun onOpen(session: Session, @PathParam("tkrmId") tkrmId: String) {

        this.session = session
        this.tkrmId = tkrmId

        updateTalkMsg(tkrmId)

        val sendObject = JSONArray(talkMsg).toString()

        session.asyncRemote.sendText(sendObject)
    }

  @OnMessage
  fun onMessage(session: Session, data: String, @PathParam("tkrmId") tkrmId: String) {
    if (data.startsWith("hello|")) {
      this.userId = data.split("hello|")[1]
      chatSockets[this.userId!!] = this // WebSocket 연결을 Map에 추가
      return
    }

    var addMsg: TalkMsg
    var otherUsers: MutableList<TalkUser>
    val message: String?

    if (data.contains("attcId:")) {
      val idx = data.indexOf("|")
      val msgIdx = data.lastIndexOf("|")
      val userId = data.substring(0, idx)
      message = data.substring(msgIdx + 1)
      val attcId = data.substring(idx + 8, msgIdx)
      val attcIdResponse = Gson().fromJson(attcId, AttcIdResponse::class.java)

      managedExecutor.runAsync {
        addMsg = talkMsgRepository.insertFile(message, attcIdResponse.attcGrpId, tkrmId, userId)
        otherUsers = talkUserRepository.findOtherUsersByTkrmId(tkrmId, userId) as MutableList<TalkUser>
        handlePostMessage(session, tkrmId, addMsg, otherUsers, userId, message)
      }
    } else {
      val idx = data.indexOf("|")
      val userId = data.substring(0, idx)
      message = data.substring(idx + 1)

      managedExecutor.runAsync {
        addMsg = talkMsgRepository.insertMessage(message, tkrmId, userId)
        otherUsers = talkUserRepository.findOtherUsersByTkrmId(tkrmId, userId) as MutableList<TalkUser>
        handlePostMessage(session, tkrmId, addMsg, otherUsers, userId, message)
      }
    }
  }

    private fun handlePostMessage(
        session: Session,
        tkrmId: String,
        addMsg: TalkMsg,
        otherUsers: MutableList<TalkUser>,
        userId: String,
        message: String?
    ) {
        chatSockets.values // 모든 WebSocket 연결에 메시지 전송
            .filter { it.tkrmId == tkrmId }
            .forEach {
                it.session.asyncRemote.sendText(JsonObject.mapFrom(addMsg).toString())
            }

        firebaseService.sendMessageMultiDevice(userId, message, userId)

        otherUsers.forEach {
            session.asyncRemote.sendText(it.id?.userId)
        }
    }

    @OnClose
    fun onClose(session: Session, @PathParam("tkrmId") tkrmId: String) {
        chatSockets.remove(tkrmId) // WebSocket 연결을 Map에서 제거
    }

    private fun updateTalkMsg(tkrmId: String) {
        val resultList = runBlocking {
            withContext(Dispatchers.IO) {
                talkMsgRepository.findChatDetail(tkrmId)
            }
        } as MutableList<TalkMsgDto>
        talkMsg = resultList
    }

    private fun sendMsg(msg: TalkMsg, tkrmId: String, userId: String){
        val talkRoomResponse: TalkRoomResponse?
        val talkUsers: List<TalkUser>

        runBlocking(Dispatchers.IO) {
            talkRoomResponse = talkRoomRepository.findTalkRoomResponseByTkrmId(tkrmId, userId)
            talkUsers = talkUserRepository.findUsersByTkrmId(tkrmId)
        }

        chatSockets
            .forEach {
                it.value.session.asyncRemote.sendText(JsonObject.mapFrom(msg).toString())
            }

        talkUsers
            .forEach{
                if (chatSockets[it.id?.userId] != null) {
                    chatSockets[it.id?.userId]?.session?.asyncRemote?.sendText(JsonObject.mapFrom(talkRoomResponse).toString())
                    firebaseService.sendMessageMultiDevice(userId, msg.msg, it.id?.userId!!)
                }
            }
    }

}