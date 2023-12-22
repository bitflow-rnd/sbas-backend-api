package org.sbas.entities.base

import org.sbas.dtos.BaseCodeSaveReq
import org.sbas.dtos.BaseCodeUpdateReq
import org.sbas.entities.CommonEntity
import java.io.Serializable
import jakarta.persistence.*

/**
 * 기초 코드
 */
@Entity
@Table(name = "base_code")
class BaseCode(
    @EmbeddedId
    var id: BaseCodeId,

    @Column(name = "cd_grp_nm", length = 100)
    var cdGrpNm: String? = null, // 코드 그룹 이름

    @Column(name = "cd_nm", length = 100)
    var cdNm: String? = null, // 코드 이름

    @Column(name = "cd_val", length = 200)
    var cdVal: String? = null, // 코드 값

    @Column(name = "cd_seq")
    var cdSeq: Int? = null, // 코드 순번

    @Column(name = "rmk", length = 200)
    var rmk: String? = null, // 비고
) : CommonEntity() {

    fun changeBaseCodeGrpNm(cdGrpNm: String?) {
        this.cdGrpNm = cdGrpNm ?: this.cdGrpNm
    }

    fun changeBaseCodeGrp(cdGrpNm: String?, rmk: String?) {
        this.cdGrpNm = cdGrpNm ?: this.cdGrpNm
        this.rmk = rmk ?: this.rmk
    }

    fun changeBaseCode(updateReq: BaseCodeUpdateReq) {
        this.cdNm = updateReq.cdNm
        this.cdVal = updateReq.cdVal
        this.cdSeq = updateReq.cdSeq
        this.rmk = updateReq.rmk
    }

    fun isCdGrpNmMatch(saveReq: BaseCodeSaveReq): Boolean {
        return saveReq.cdGrpNm == this.cdGrpNm
    }
}

@Embeddable
data class BaseCodeId(
    @Column(name = "cd_grp_id", nullable = false, length = 8)
    var cdGrpId: String, // 코드 그룹 ID

    @Column(name = "cd_id", nullable = false, length = 8)
    var cdId: String, // 코드 ID
) : Serializable {

    companion object {
        private const val serialVersionUID = -165979700881194617L
    }
}