package org.sbas.dtos

import org.sbas.utils.annotation.NoArg

@NoArg
class TalkRoomDto (
  val tkrmId: String,
  var tkrmNm: String,
  val cretDt: String?,
  val cretTm: String?,
  val cretUserId: String?
)