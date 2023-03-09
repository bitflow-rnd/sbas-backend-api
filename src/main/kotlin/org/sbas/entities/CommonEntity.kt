package org.sbas.entities

import kotlinx.serialization.Serializable
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.sbas.serializers.TimestampSerializer
import java.time.Instant
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@Serializable
@MappedSuperclass
abstract class CommonEntity (
    @Column(name = "rgst_user_id", nullable = false, length = 10)
    var rgstUserId: String? = null,

    @Column(name = "rgst_dttm", nullable = false, updatable = false)
    @CreationTimestamp
    @Serializable(with = TimestampSerializer::class)
    var rgstDttm: Instant? = null,

    @Column(name = "updt_user_id", nullable = false, length = 10)
    var updtUserId: String? = null,

    @Column(name = "updt_dttm", nullable = false)
    @UpdateTimestamp
    @Serializable(with = TimestampSerializer::class)
    var updtDttm: Instant? = null,

) : java.io.Serializable {

    companion object {
        private const val serialVersionUID: Long = 5417401260223889229L
    }
}