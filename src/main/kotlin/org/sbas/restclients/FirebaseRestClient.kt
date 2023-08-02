package org.sbas.restclients

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import io.quarkus.runtime.StartupEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jboss.logging.Logger
import org.sbas.entities.info.InfoUser
import org.sbas.parameters.SendPushRequest
import org.sbas.repositories.InfoUserRepository
import org.sbas.responses.CommonResponse
import java.io.FileInputStream
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.inject.Inject
import javax.ws.rs.NotFoundException
import javax.ws.rs.POST
import javax.ws.rs.Path

@ApplicationScoped
class FirebaseService {

    @Inject
    lateinit var log: Logger

    @Inject
    private lateinit var userRepository: InfoUserRepository
    fun onStart(@Observes ev: StartupEvent) {
        if (FirebaseApp.getApps().isEmpty()) {
            val serviceAccount =
                FileInputStream("C:\\sbas\\www\\public\\firebase\\serviceAccountKey.json")

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("sbas-4c928.appspot.com")
                .build()

            FirebaseApp.initializeApp(options)
        }
    }

    fun sendMessage(title: String, body: String?, to: String) {
        val findUser: InfoUser?
        runBlocking(Dispatchers.IO) {
            findUser = userRepository.findByUserId(to) ?: throw NotFoundException("ID를 찾을 수 없습니다.")
        }

        val token = findUser!!.pushKey

        val notification = Notification
            .builder()
            .setTitle(title)
            .setBody(body)
            .build()

        val message = Message.builder()
            .setNotification(notification)
            .setToken(token)
            .build()

        val messageId = FirebaseMessaging.getInstance().send(message)
        log.debug("Sent message with ID: $messageId")
    }
}

@Path("/firebase/send")
class SendMessageResource {

    @Inject
    lateinit var firebaseService: FirebaseService

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var userRepository: InfoUserRepository

    @POST
    fun send(sendPushRequest: SendPushRequest): CommonResponse<String> {
        val findUser = userRepository.findByUserId(sendPushRequest.to) ?: throw NotFoundException("ID를 찾을 수 없습니다.")

        val notification = Notification.builder()
            .setTitle(sendPushRequest.from)
            .setBody(sendPushRequest.msg)
            .build()

        val message = Message.builder()
            .setNotification(notification)
            .setToken(findUser.pushKey)
            .build()

        val messageId = FirebaseMessaging.getInstance().send(message)

        return CommonResponse(messageId)
    }
}