package org.sbas.dtos.info

import org.sbas.utils.annotation.NoArg

@NoArg
data class InfoAlarmDto (
  val alarmId: Int,
  val title: String,
  val detail: String,
  val senderId: String,
  val senderName: String,
  val receiverId: String,
  val receiverName: String,
  val isRead: Boolean,
  val rgstDttm: String,
)