package org.sbas.utils

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import io.quarkus.runtime.StartupEvent
import org.jboss.logging.Logger
import java.io.FileInputStream
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@ApplicationScoped
class FirebaseService {
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

    fun sendMessage(title: String, body: String, token: String) {
        val message = Message.builder()
            .putData("title", title)
            .putData("body", body)
            .setToken(token)
            .build()

        val messageId = FirebaseMessaging.getInstance().send(message)
        println("Sent message with ID: $messageId")
    }
}

@NoArg
data class MessageRequest(val title: String, val body: String, val token: String)

@Path("/send")
class SendMessageResource {
    @Inject
    lateinit var firebaseService: FirebaseService
    @Inject
    lateinit var log: Logger

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    fun send(messageRequest: MessageRequest): String {
        firebaseService.sendMessage(messageRequest.title, messageRequest.body, messageRequest.token)
        return "Message sent"
    }
}