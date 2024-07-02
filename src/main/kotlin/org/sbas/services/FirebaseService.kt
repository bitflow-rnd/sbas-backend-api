package org.sbas.services

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import org.sbas.entities.info.InfoUser
import org.sbas.entities.info.UserFcmToken
import org.sbas.repositories.InfoUserRepository
import org.sbas.repositories.UserFcmTokenRepository
import org.sbas.responses.CommonResponse

@ApplicationScoped
class FirebaseService {

  @Inject
  private lateinit var log: Logger

  @ConfigProperty(name = "firebase.account.file.path")
  private lateinit var FIREBASE_ACCOUNT_PATH: String

  @ConfigProperty(name = "firebase.project.id")
  private lateinit var FIREBASE_PROJECT_ID: String

  @Inject
  private lateinit var userRepository: InfoUserRepository

  @Inject
  private lateinit var userFcmTokenRepository: UserFcmTokenRepository

  /*
  fun getApp(): FirebaseApp {
      if (FirebaseApp.getApps().isEmpty()) {
          val serviceAccount =
          FileInputStream("C:\\www\\dev.smartbas.org\\public\\firebase\\serviceAccountKey.json")
          val options = FirebaseOptions.builder()
          .setCredentials(GoogleCredentials.fromStream(serviceAccount))
          .setStorageBucket("smart-bed-allocation-system")
          .build()
          FirebaseApp.initializeApp(options)
      }
  }
  */

  fun onStart(@Observes ev: StartupEvent) {

    if (FirebaseApp.getApps().isEmpty()) {
      try {
        val loader = Thread.currentThread().getContextClassLoader()
        val serviceAccount = loader.getResourceAsStream(FIREBASE_ACCOUNT_PATH)
        val credentials = GoogleCredentials.fromStream(serviceAccount)
        log.debug("cred $credentials")
        val options = FirebaseOptions.builder()
          .setCredentials(credentials)
          .setProjectId(FIREBASE_PROJECT_ID)
          .build()
        FirebaseApp.initializeApp(options)
      } catch (e: Exception) {
        log.error("err ${e.localizedMessage}")
      }
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

  fun sendMessageMultiDevice(title: String, body: String?, userId: String) {

    val pushKeys: List<UserFcmToken> = userFcmTokenRepository.findAllByUserId(userId)
    val tokens = pushKeys.map { it.reregistrationToken }

    val notification = Notification.builder()
      .setTitle(title)
      .setBody(body)
      .build()

    val message = MulticastMessage.builder()
      .setNotification(notification)
      .addAllTokens(tokens)
      .build()

    val response = FirebaseMessaging.getInstance().sendMulticast(message)

    response.responses.forEachIndexed { index, sendResponse ->
      if (!sendResponse.isSuccessful) {
        pushKeys[index].invalidateToken()
      }
    }
  }

  fun sendMessageMultiDeviceV2(title: String, body: String?, userIdList: List<String>) {
    val pushKeys: List<UserFcmToken> = userFcmTokenRepository.findAllByUserIdList(userIdList)

    val tokens = pushKeys.map { it.reregistrationToken }

    val notification = Notification.builder()
      .setTitle(title)
      .setBody(body)
      .build()

    val message = MulticastMessage.builder()
      .setNotification(notification)
      .addAllTokens(tokens)
      .build()

    val response = FirebaseMessaging.getInstance().sendMulticast(message)

    response.responses.forEachIndexed { index, sendResponse ->
      if (!sendResponse.isSuccessful) {
        pushKeys[index].invalidateToken()
      }
    }
  }

  @Transactional
  fun addPushKey(userId: String, pushKey: String): CommonResponse<String> {
    val userFcmToken = UserFcmToken(userId = userId, reregistrationToken = pushKey)
    val findUserFcmTokens = userFcmTokenRepository.findAllByUserId(userId)
    val isDuplicate = findUserFcmTokens.any { it.reregistrationToken == pushKey }
    if (!isDuplicate) {
      userFcmTokenRepository.persist(userFcmToken)
    }
    return CommonResponse("push key가 등록되었습니다.")
  }

}