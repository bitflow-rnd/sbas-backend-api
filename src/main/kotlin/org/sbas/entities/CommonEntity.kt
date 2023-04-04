package org.sbas.entities

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.sbas.utils.CommonEntityListener
import java.time.Instant
import javax.persistence.*

@MappedSuperclass
@EntityListeners(CommonEntityListener::class)
abstract class CommonEntity (
    @Column(name = "rgst_user_id", nullable = false, length = 15, updatable = false)
    var rgstUserId: String? = null, // 등록 사용자 ID

    @Column(name = "rgst_dttm", nullable = false, updatable = false)
    @CreationTimestamp
    var rgstDttm: Instant? = null, // 등록 일시

    @Column(name = "updt_user_id", nullable = false, length = 15)
    var updtUserId: String? = null, // 수정 사용자 ID

    @Column(name = "updt_dttm", nullable = false)
    @UpdateTimestamp
    var updtDttm: Instant? = null, // 수정 일시
)