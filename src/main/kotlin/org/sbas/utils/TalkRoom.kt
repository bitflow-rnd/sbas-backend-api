package org.sbas.utils

import io.vertx.core.json.JsonObject
import kotlinx.coroutines.*
import org.jboss.logging.Logger
import org.sbas.entities.talk.TalkMsg
import org.sbas.entities.talk.TalkUser
import org.sbas.entities.talk.arrToJson
import org.sbas.repositories.TalkMsgRepository
import org.sbas.repositories.TalkRoomRepository
import org.sbas.repositories.TalkUserRepository
import org.sbas.responses.messages.TalkRoomResponse
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint


@ServerEndpoint("/chat-rooms/{tkrmId}/{userId}")
@ApplicationScoped
class TalkRoom {

    companion object {
        private val chatSockets = mutableMapOf<String, Session>() // WebSocket 연결을 관리할 Map
        private lateinit var talkMsg: MutableList<TalkMsg>
    }

    private lateinit var session: Session // WebSocket 세션
    private lateinit var tkrmId: String // 채팅방 ID
    private lateinit var userId: String // 사용자 ID

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var talkMsgRepository: TalkMsgRepository

    @Inject
    lateinit var talkUserRepository: TalkUserRepository

    @Inject
    private lateinit var talkRoomRepository: TalkRoomRepository

    @OnOpen
    fun onOpen(session: Session, @PathParam("tkrmId") tkrmId: String, @PathParam("userId") userId: String) {
        updateTalkMsg(tkrmId)

        val sendObject = arrToJson(talkMsg)
        session.asyncRemote.sendText(sendObject)

        chatSockets[userId] = session // WebSocket 연결을 Map에 추가

    }

    @OnMessage
    fun onMessage(session: Session, message: String, @PathParam("tkrmId") tkrmId: String, @PathParam("userId") userId: String) {
        var addMsg: TalkMsg
        runBlocking(Dispatchers.IO) {
            addMsg = talkMsgRepository.insertMessage(message, tkrmId, userId)
        }

        val talkRoomResponse: TalkRoomResponse?
        val talkUsers: List<TalkUser>

        runBlocking(Dispatchers.IO) {
            talkRoomResponse = talkRoomRepository.findTalkRoomResponseByTkrmId(tkrmId)
            talkUsers = talkUserRepository.findUsersByTkrmId(tkrmId)
        }

        chatSockets
            .forEach {
                it.value.asyncRemote.sendText(JsonObject.mapFrom(addMsg).toString())
//                TalkRoomList.chatRoomsSockets[it.key]?.asyncRemote?.sendText(JsonObject.mapFrom(talkRoomResponse).toString())
            }

        talkUsers
            .forEach{
                TalkRoomList.chatRoomsSockets[it.id?.userId]?.asyncRemote?.sendText(JsonObject.mapFrom(talkRoomResponse).toString())
            }

    }

    @OnClose
    fun onClose(session: Session, @PathParam("userId") userId: String, @PathParam("tkrmId") tkrmId: String) {
        chatSockets.remove(userId) // WebSocket 연결을 Map에서 제거
    }

    private fun updateTalkMsg(tkrmId: String) {
        val resultList = runBlocking {
            withContext(Dispatchers.IO) {
                talkMsgRepository.findChatDetail(tkrmId)
            }
        } as MutableList<TalkMsg>
        talkMsg = resultList
    }
}