package org.sbas.utils

import io.vertx.core.json.JsonObject
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.websocket.OnMessage
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jboss.logging.Logger
import org.json.JSONArray
import org.sbas.repositories.TalkRoomRepository
import org.sbas.repositories.TalkUserRepository
import org.sbas.responses.messages.TalkRoomResponse

@ServerEndpoint(value = "/chat-rooms/{userId}")
@ApplicationScoped
class TalkRoomList {

    companion object {
        val chatRoomsSockets = mutableMapOf<String, Session>()
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
        updateTalkRooms(userId)

        val sendObject = JSONArray(talkRooms).toString()

        session.asyncRemote.sendText(sendObject)

        chatRoomsSockets[userId] = session
    }

    @OnMessage
    fun onMessage(session: Session, tkrmId: String, @PathParam("userId") userId: String) {
        val talkRoomResponse: TalkRoomResponse?

        runBlocking(Dispatchers.IO) {
            talkRoomResponse = talkRoomRepository.findTalkRoomResponseByTkrmId(tkrmId, userId)
        }

        chatRoomsSockets.forEach{
            it.value.asyncRemote.sendText(JsonObject.mapFrom(talkRoomResponse).toString())
        }

    }

    private fun updateTalkRooms(userId: String){
        val resultList = runBlocking(Dispatchers.IO) {
            talkRoomRepository.findTalkRoomResponse(userId)
        } as MutableList<TalkRoomResponse>
        talkRooms = resultList
    }

}