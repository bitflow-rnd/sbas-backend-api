package org.sbas.utils

import io.vertx.core.json.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jboss.logging.Logger
import org.sbas.entities.talk.TalkUser
import org.sbas.repositories.TalkRoomRepository
import org.sbas.repositories.TalkUserRepository
import org.sbas.responses.messages.TalkRoomResponse
import org.sbas.responses.messages.arrToJson
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
        session.maxIdleTimeout = -1

        updateTalkRooms(userId)

        val sendObject = arrToJson(talkRooms)

        session.asyncRemote.sendText(sendObject)

        chatRoomsSockets[userId] = this
    }

    @OnMessage
    fun onMessage(session: Session, message: String, @PathParam("userId") userId: String) {
        val talkUsers: MutableList<TalkUser>
        val talkRoomResponse: TalkRoomResponse?

        runBlocking(Dispatchers.IO) {
            talkUsers = talkUserRepository.findUsersByTkrmId(message) as MutableList<TalkUser>
            talkRoomResponse = talkRoomRepository.findTalkRoomResponseByTkrmId(message)
        }

        talkUsers.forEach{
            chatRoomsSockets[it.id?.userId]?.session?.asyncRemote?.sendText(JsonObject.mapFrom(talkRoomResponse).toString())
        }

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

}