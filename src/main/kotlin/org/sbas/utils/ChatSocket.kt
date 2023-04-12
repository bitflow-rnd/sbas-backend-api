package org.sbas.utils

import io.vertx.core.json.JsonArray
import kotlinx.coroutines.*
import org.jboss.logging.Logger
import org.sbas.entities.talk.TalkMsg
import org.sbas.repositories.TalkMsgRepository
import org.sbas.repositories.TalkRoomRepository
import org.sbas.repositories.TalkUserRepository
import org.sbas.responses.messages.TalkRoomResponse
import javax.inject.Inject
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint


@ServerEndpoint("/chat-rooms/{tkrmId}/{userId}")
class ChatSocket {

    companion object {
        lateinit var talkMsg: List<TalkMsg>
    }

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var talkMsgRepository: TalkMsgRepository

    @OnOpen
    fun onOpen(session: Session, @PathParam("tkrmId") tkrmId: String, @PathParam("userId") userId: String) {
        updateTalkMsg(tkrmId)
        val sendObject = JsonArray.of(talkMsg).toString()
        session.asyncRemote.sendText(sendObject)
    }

    @OnMessage
    fun onMessage(session: Session, message: String, @PathParam("tkrmId") tkrmId: String, @PathParam("userId") userId: String) {
        runBlocking(Dispatchers.IO) {
            talkMsgRepository.insertMessage(message, tkrmId, userId)
        }
        updateTalkMsg(tkrmId)
        val sendObject = JsonArray.of(talkMsg).toString()
        session.asyncRemote.sendText(sendObject)
    }

    fun updateTalkMsg(tkrmId: String) {
        val resultList = runBlocking{
            withContext(Dispatchers.IO) {
                talkMsgRepository.findChatDetail(tkrmId)
            } }
        talkMsg = resultList
    }
}

@ServerEndpoint(value = "/chat-rooms/{userId}")
class ChatRoomEndpoint {

    companion object {
        lateinit var talkRooms: List<TalkRoomResponse>
    }

    @Inject
    private lateinit var log: Logger

    @Inject
    private lateinit var talkRoomRepository: TalkRoomRepository

    @Inject
    private lateinit var talkUserRepository: TalkUserRepository

    @OnOpen
    fun onOpen(session: Session, @PathParam("userId") userId: String) {
        updateTalkRooms(userId)
        val sendObject = JsonArray.of(talkRooms).toString()
        session.asyncRemote.sendText(sendObject)
    }

    @OnClose
    fun onClose(session: Session, @PathParam("userId") userId: String) {

    }

    private fun updateTalkRooms(userId: String){
        val resultList = runBlocking(Dispatchers.IO) {
            talkRoomRepository.findTalkRoomResponse(userId)
        }
        talkRooms = resultList
    }

}