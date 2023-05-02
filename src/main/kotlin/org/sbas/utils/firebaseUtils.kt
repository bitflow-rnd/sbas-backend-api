package org.sbas.utils

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import io.quarkus.runtime.StartupEvent
import java.io.FileInputStream
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path

@ApplicationScoped
class FirebaseService {
    lateinit var firebaseMessaging: FirebaseMessaging

    fun onStart(@Observes event: StartupEvent) {
        val serviceAccount = FileInputStream("C:/Users/plela/Downloads/sbas-4c928-firebase-adminsdk-1rbzo-51e04a6c61.json")

        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        FirebaseApp.initializeApp(options)
        firebaseMessaging = FirebaseMessaging.getInstance()
    }
}

@Path("/firebase")
class FirebaseResource @Inject constructor(private val firebaseService: FirebaseService) {

    @POST
    @Path("/send-message")
    fun sendMessage(): String {
        val message = Message.builder()
            .putData("title", "Hello from Kotlin Quarkus!")
            .putData("body", "Firebase Cloud Messaging is working.")
            .setTopic("your_topic")
            .build()

        val response = firebaseService.firebaseMessaging.send(message)
        return "Sent message: $response"
    }
}