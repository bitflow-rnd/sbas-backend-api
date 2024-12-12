package org.sbas.entities.info

import jakarta.persistence.*
import org.sbas.constants.enums.DsbdType
import org.sbas.entities.CommonEntity

@Entity
@Table(name = "info_dsbd")
class InfoDsbd(
  @Id
  @Column(name = "dsbd_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var dsbdId: Long = 0L,

  @Column(name = "user_id", nullable = false)
  var userId: String,

  @Column(name = "dsbd_type", nullable = false)
  @Enumerated(EnumType.STRING)
  var dsbdType: DsbdType,

  @Column(name = "title", nullable = false)
  var title: String,

  @Column(name = "value", nullable = false)
  var value: String,

  @Column(name = "before_value", nullable = false)
  var beforeValue: String,

) : CommonEntity()