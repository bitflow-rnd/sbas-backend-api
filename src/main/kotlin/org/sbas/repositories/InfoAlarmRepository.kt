package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.jboss.logging.Logger
import org.sbas.entities.info.InfoAlarm

@ApplicationScoped
class InfoAlarmRepository : PanacheRepositoryBase<InfoAlarm, Int> {

  @Inject
  private lateinit var log: Logger

  fun findInfoAlarmByReceiverId(receiverId: String): List<InfoAlarm> {
    return find("receiverId = '$receiverId'").list()
  }

  fun findUnreadAlarmsByReceiverId(receiverId: String): Long {
    return find("receiverId = '$receiverId' and isRead = false").count()
  }

}