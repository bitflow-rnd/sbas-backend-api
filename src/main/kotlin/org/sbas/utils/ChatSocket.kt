package org.sbas.utils

import org.eclipse.microprofile.jwt.JsonWebToken
import org.sbas.responses.messages.TalkRoomResponse
import org.sbas.services.TalkService
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

@ServerEndpoint("/chat-rooms/{tkrmId}/{userId}")
@ApplicationScoped
class ChatSocket {

    companion object {
        private val chatRooms: MutableMap<String, ChatRoom> = ConcurrentHashMap()
    }

    private lateinit var chatRoom: ChatRoom

    @OnOpen
    fun onOpen(session: Session, @PathParam("tkrmId") tkrmId: String, @PathParam("userId") userId: String) {
        chatRoom = chatRooms.getOrPut(tkrmId) { ChatRoom(tkrmId) }
        chatRoom.addSession(userId, session)
        chatRoom.broadcast("$userId joined the chat")
        updateChatRoomList()
    }

    @OnClose
    fun onClose(session: Session?, @PathParam("userId") userId: String) {
        chatRoom.removeSession(userId)
        chatRoom.broadcast("$userId left the chat")
        if (chatRoom.isEmpty()) {
            chatRooms.remove(chatRoom.id)
        }
        updateChatRoomList()
    }

    @OnError
    fun onError(session: Session?, @PathParam("userId") userId: String, throwable: Throwable) {
        chatRoom.removeSession(userId)
        chatRoom.broadcast("$userId left the chat on error: $throwable")
        if (chatRoom.isEmpty()) {
            chatRooms.remove(chatRoom.id)
        }
        updateChatRoomList()
    }

    @OnMessage
    fun onMessage(message: String, @PathParam("userId") userId: String) {
        if (message.equals("_ready_", ignoreCase = true)) {
            chatRoom.broadcast("$userId joined the chat")
        } else {
            chatRoom.broadcast(">> $userId: $message")
        }
    }

    private fun updateChatRoomList() {
        val chatRoomList = chatRooms.values.map { room ->
            room.getLastChat(room.id)
        }
        chatRooms.values.forEach { room ->
            room.sessions.values.forEach(Consumer { s: Session ->
                s.asyncRemote.sendObject(chatRoomList) { result: SendResult ->
                    if (result.exception != null) {
                        println("Unable to send message: " + result.exception)
                    }
                }
            })
        }
    }

    class ChatRoom(val id: String) {

        @Inject
        lateinit var jwt: JsonWebToken

        @Inject
        private lateinit var talkService: TalkService

        val sessions: MutableMap<String, Session> = ConcurrentHashMap()

        fun addSession(userId: String, session: Session) {
            sessions[userId] = session
        }

        fun removeSession(userId: String) {
            sessions.remove(userId)
        }

        fun isEmpty(): Boolean = sessions.isEmpty()

        fun broadcast(message: String) {
            sessions.values.forEach(Consumer { s: Session ->
                s.asyncRemote.sendObject(message) { result: SendResult ->
                    if (result.exception != null) {
                        println("Unable to send message: " + result.exception)
                    }
                }
            })
        }

        fun getLastChat(tkrmId: String): TalkRoomResponse {
            return talkService.getMyChatByTkrmId(tkrmId)
        }
    }
}