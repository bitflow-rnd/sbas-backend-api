package org.sbas.utils

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jboss.logging.Logger
import org.sbas.repositories.TalkRoomRepository
import org.sbas.repositories.TalkUserRepository
import org.sbas.responses.messages.TalkRoomResponse
import javax.inject.Inject
import javax.websocket.OnClose
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

@ServerEndpoint(value = "/chat-rooms/{userId}")
class TalkRoomList {

    companion object {
        private val chatRoomsSockets = mutableMapOf<String, TalkRoomList>()
        lateinit var talkRooms: MutableList<TalkRoomResponse>
    }

    private lateinit var session: Session
    private lateinit var userId: String

    @Inject
    private lateinit var log: Logger

    @Inject
    private lateinit var talkRoomRepository: TalkRoomRepository

    @Inject
    private lateinit var talkUserRepository: TalkUserRepository

    @OnOpen
    fun onOpen(session: Session, @PathParam("userId") userId: String) {
        this.session = session
        this.userId = userId

        updateTalkRooms(userId)

        val sendObject = JsonArray.of(talkRooms).toString()
        session.asyncRemote.sendText(sendObject)

        chatRoomsSockets[userId] = this
    }

    @OnMessage
    fun onMessage(session: Session, message: String, @PathParam("userId") userId: String) {
        val talkRoomResponse = talkRoomRepository.findTalkRoomResponseByTkrmId(message)

        session.asyncRemote.sendText(JsonObject.mapFrom(talkRoomResponse).toString())
    }


    @OnClose
    fun onClose(session: Session, @PathParam("userId") userId: String) {

    }

    private fun updateTalkRooms(userId: String){
        val resultList = runBlocking(Dispatchers.IO) {
            talkRoomRepository.findTalkRoomResponse(userId)
        } as MutableList<TalkRoomResponse>
        talkRooms = resultList
    }

    fun getChatRoomsSockets(): MutableMap<String, TalkRoomList> {
        return chatRoomsSockets
    }

}