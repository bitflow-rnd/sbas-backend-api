package org.sbas.entities.info

import org.sbas.entities.CommonEntity
import java.io.Serializable
import javax.persistence.*

/**
 * 연락처 정보
 */
@Entity
@Table(name = "info_cntc")
class InfoCntc(
    @EmbeddedId
    var id: InfoCntcId,

    @Column(name = "cntc_type", nullable = false, length = 8)
    var cntcType: String, // 연락처 유형
) : CommonEntity()

@Embeddable
data class InfoCntcId(
    @Column(name = "user_id", nullable = false, length = 15)
    var userId: String, // 사용자 ID

    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String, // 이력 코드

    @Column(name = "hist_seq", nullable = false)
    var histSeq: Int, // 이력 순번

    @Column(name = "mbr_id", nullable = false, length = 15)
    var mbrId: String, // 회원 ID
) : Serializable {

    companion object {
        private const val serialVersionUID: Long = -3847080031833897688L
    }
}