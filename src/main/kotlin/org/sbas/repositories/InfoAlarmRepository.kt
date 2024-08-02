package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import org.jboss.logging.Logger
import org.sbas.entities.info.InfoAlarm

@ApplicationScoped
class InfoAlarmRepository : PanacheRepositoryBase<InfoAlarm, Int> {

  @Inject
  private lateinit var log: Logger

  fun findAllByReceiverId(receiverId: String): List<InfoAlarm> {
    return find("receiverId = '$receiverId'").list()
  }

  fun findUnreadAlarmsByReceiverId(receiverId: String): Long {
    return find("receiverId = '$receiverId' and isRead = false").count()
  }

  @Transactional
  fun readAlarmsByReceiverId(receiverId: String): Int {
    return update("isRead = true where receiverId = '$receiverId' and isRead = false")
  }

}