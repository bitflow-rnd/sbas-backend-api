package org.sbas.entities

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import kotlinx.serialization.Serializable
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.sbas.serializers.TimestampSerializer
import java.time.Instant
import java.util.UUID
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "base_code")
@Serializable
class BaseCode (
    @EmbeddedId
    var id: BaseCodeId? = null,

    @Column(name = "cd_grp_nm", length = 100)
    var cdGrpNm: String? = null,

    @Column(name = "cd_nm", length = 100)
    var cdNm: String? = null,

    @Column(name = "cd_val", length = 200)
    var cdVal: String? = null,

    @Column(name = "cd_seq", precision = 3)
    var cdSeq: Long? = null,

    @Column(name = "rmk", length = 200)
    var rmk: String? = null,

    @Column(name = "rgst_user_id", nullable = false, length = 10)
    var rgstUserId: String? = null,

    @Column(name = "rgst_dttm", nullable = false)
    @Serializable(with = TimestampSerializer::class)
    var rgstDttm: Instant? = null,

    @Column(name = "updt_user_id", nullable = false, length = 10)
    var updtUserId: String? = null,

    @Column(name = "updt_dttm", nullable = false)
    @Serializable(with = TimestampSerializer::class)
    var updtDttm: Instant? = null
) : PanacheEntityBase, java.io.Serializable {
    companion object {
        private const val serialVersionUID = 165979703881194617L
    }
}
