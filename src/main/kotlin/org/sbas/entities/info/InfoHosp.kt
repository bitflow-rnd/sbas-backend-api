package org.sbas.entities.info

import org.sbas.entities.CommonEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * 병원 정보
 */
@Entity
@Table(name = "info_hosp")
class InfoHosp(
    @Id
    @Column(name = "hosp_id", nullable = false, length = 10)
    var id: String? = null, // 병원 ID

    @Column(name = "hosp_id_egen", length = 10)
    var hospIdEgen: String? = null, // 병원 ID (E-GEN)

    @Column(name = "attr_id", nullable = false, length = 10)
    var attrId: String? = null, // 속성 ID

    @Column(name = "attr_val", length = 200)
    var attrVal: String? = null, // 속성 값

    @Column(name = "rmk", length = 200)
    var rmk: String? = null, // 비고
) : CommonEntity()