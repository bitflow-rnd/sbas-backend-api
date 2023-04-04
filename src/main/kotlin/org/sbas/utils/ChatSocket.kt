package org.sbas.utils

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint


@ServerEndpoint("/chat/{tkrmId}")
@ApplicationScoped
class ChatSocket {

    var sessions: MutableMap<String, Session> = ConcurrentHashMap<String, Session>()

    @OnOpen
    fun onOpen(session: Session, @PathParam("tkrmId") tkrmId: String) {
        sessions[tkrmId] = session
    }

    @OnClose
    fun onClose(session: Session?, @PathParam("tkrmId") tkrmId: String) {
        sessions.remove(tkrmId)
        broadcast("User $tkrmId left")
    }

    @OnError
    fun onError(session: Session?, @PathParam("tkrmId") tkrmId: String, throwable: Throwable) {
        sessions.remove(tkrmId)
        broadcast("User $tkrmId left on error: $throwable")
    }

    @OnMessage
    fun onMessage(message: String, @PathParam("tkrmId") tkrmId: String) {
        if (message.equals("_ready_", ignoreCase = true)) {
            broadcast("User $tkrmId joined")
        } else {
            broadcast(">> $tkrmId: $message")
        }
    }

    private fun broadcast(message: String) {
        sessions.values.forEach(Consumer<Session> { s: Session ->
            s.asyncRemote.sendObject(message) { result ->
                if (result.exception != null) {
                    println("Unable to send message: " + result.exception)
                }
            }
        })
    }

}