package org.sbas.entities.info

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "notice_read_status")
class NoticeReadStatus (

    @EmbeddedId
    var id: NoticeReadStatusId,

    @Column(name = "read_dt", nullable = false, length = 8)
    var readDt: String? = null,

    @Column(name = "read_tm", nullable = false, length = 4)
    var readTm: String? = null,

)

@Embeddable
data class NoticeReadStatusId(
    @Column(name = "user_id", nullable = false, length = 15)
    var userId: String,

    @Column(name = "notice_id", nullable = false, length = 2)
    var noticeId: String,
) : Serializable {
    companion object{
        private const val serialVersionUID: Long = -7970134900305544117L
    }
}