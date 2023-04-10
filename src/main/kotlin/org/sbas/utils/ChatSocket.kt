package org.sbas.utils

import org.sbas.responses.messages.TalkRoomResponse
import org.sbas.services.TalkService
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

@ServerEndpoint("/chat-rooms/{userId}/{tkrmId}")
@ApplicationScoped
class ChatSocket {

    companion object {
        private val chatRooms: MutableMap<String, ChatRoom> = ConcurrentHashMap()
    }

    private lateinit var chatRoom: ChatRoom

    @Inject
    lateinit var talkService: TalkService

    @OnOpen
    fun onOpen(session: Session, @PathParam("tkrmId") tkrmId: String, @PathParam("userId") userId: String) {
        chatRoom = chatRooms.getOrPut(tkrmId) { ChatRoom(tkrmId) }
        chatRoom.addSession(userId, session)
        chatRoom.broadcast("$userId joined the chat")
        updateChatRoomList(userId)
    }

    @OnClose
    fun onClose(session: Session?, @PathParam("userId") userId: String) {
        chatRoom.removeSession(userId)
        chatRoom.broadcast("$userId left the chat")
        if (chatRoom.isEmpty()) {
            chatRooms.remove(chatRoom.id)
        }
        updateChatRoomList(userId)
    }

    @OnError
    fun onError(session: Session?, @PathParam("userId") userId: String, throwable: Throwable) {
        chatRoom.removeSession(userId)
        chatRoom.broadcast("$userId left the chat on error: $throwable")
        if (chatRoom.isEmpty()) {
            chatRooms.remove(chatRoom.id)
        }
        updateChatRoomList(userId)
    }

    @OnMessage
    fun onMessage(message: String, @PathParam("userId") userId: String) {
        if (message.equals("_ready_", ignoreCase = true)) {
//            chatRoom.broadcast("$userId joined the chat")
            chatRoom.broadcast("hi")
        } else {
//            chatRoom.broadcast(">> $userId: $message")
//            talkService.sendMsg(chatRoom.id, userId, message)
        }
    }

    private fun updateChatRoomList(userId: String) {
        val chatRoomList = chatRooms.values.filter { room ->
            room.sessions.keys.contains(userId)
        }.map { room ->
            room.getLastChat(room.id)
        }
        chatRoom.sessions[userId]?.asyncRemote?.sendObject(chatRoomList) { result: SendResult ->
            if (result.exception != null) {
                println("Unable to send message: " + result.exception)
            }
        }
    }

    class ChatRoom(val id: String) {

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