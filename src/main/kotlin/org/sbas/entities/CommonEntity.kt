package org.sbas.entities

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@MappedSuperclass
@EntityListeners(CommonEntityListener::class)
abstract class CommonEntity (
    @Column(name = "rgst_user_id", nullable = false, length = 15, updatable = false)
    var rgstUserId: String = "", // 등록 사용자 ID

    @Column(name = "rgst_dttm", nullable = false, updatable = false)
    @CreationTimestamp
    var rgstDttm: Instant = Instant.now(), // 등록 일시

    @Column(name = "updt_user_id", nullable = false, length = 15)
    var updtUserId: String = "", // 수정 사용자 ID

    @Column(name = "updt_dttm", nullable = false)
    @UpdateTimestamp
    var updtDttm: Instant = Instant.now(), // 수정 일시
)