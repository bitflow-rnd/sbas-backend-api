package org.sbas.entities.info

import org.sbas.entities.CommonEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * 기관 정보
 */
@Entity
@Table(name = "info_inst")
class InfoInst(
    @Id
    @Column(name = "inst_id", nullable = false, length = 10)
    var id: String? = null, // 기관 ID

    @Column(name = "inst_type_cd", nullable = false, length = 8)
    var instTypeCd: String? = null, // 기관 유형 코드

    @Column(name = "inst_nm", nullable = false, length = 100)
    var instNm: String? = null, // 기관 이름

    @Column(name = "dstr_cd_1", nullable = false, length = 8)
    var dstrCd1: String? = null, // 지역 코드 (시도)

    @Column(name = "dstr_cd_2", nullable = false, length = 8)
    var dstrCd2: String? = null, // 지역 코드 (시군구)

    @Column(name = "chrg_id", nullable = false, length = 10)
    var chrgId: String? = null, // 담당자 ID

    @Column(name = "chrg_nm", nullable = false, length = 10)
    var chrgNm: String? = null, // 담당자 이름

    @Column(name = "chrg_telno", nullable = false, length = 11)
    var chrgTelno: String? = null, // 담당자 전화번호

    @Column(name = "rmk", length = 200)
    var rmk: String? = null, // 비고

    @Column(name = "attc_id", length = 10)
    var attcId: String? = null, // 첨부 ID
) : CommonEntity(), java.io.Serializable {
    companion object {
        private const val serialVersionUID: Long = -3673425429591182434L
    }
}