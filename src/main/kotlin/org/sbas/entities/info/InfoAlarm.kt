package org.sbas.entities.info

import jakarta.persistence.*
import org.sbas.entities.CommonEntity

@Entity
@Table(name = "info_alarm")
class InfoAlarm (

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "alarm_id", nullable = false)
  val alarmId : Int = 0,

  @Column(name = "title", nullable = false, length = 30)
  val title: String,

  @Column(name = "detail", nullable = false, length = 50)
  val detail: String,

  @Column(name = "sender_id", nullable = false, length = 15)
  val senderId : String,

  @Column(name = "receiver_id", nullable = false, length = 15)
  val receiverId : String,

  @Column(name = "is_read", nullable = false)
  val isRead : Boolean,

) : CommonEntity()