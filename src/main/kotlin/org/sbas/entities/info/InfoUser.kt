package org.sbas.entities.info

import org.sbas.entities.CommonEntity
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * 사용자 정보
 */
@Entity
@Table(name = "info_user")
class InfoUser(
    @Id
    @Column(name = "user_id", nullable = false, length = 10)
    var id: String? = null,  // 사용자 ID

    @Column(name = "pw", nullable = false, length = 100)
    var pw: String? = null, // 비밀번호

    @Column(name = "user_nm", nullable = false, length = 10)
    var userNm: String? = null, // 사용자 이름

    @Column(name = "user_ci", nullable = false, length = 300)
    var userCi: String? = null, // 사용자 CI

    @Column(name = "push_key", nullable = false, length = 300)
    var pushKey: String? = null, // Push Key

    @Column(name = "age", nullable = false, precision = 3)
    var age: BigDecimal? = null, // 나이

    @Column(name = "gndr", nullable = false, length = 1)
    var gndr: String? = null, //성별

    @Column(name = "telno", nullable = false, length = 11)
    var telno: String? = null, // 전화번호

    @Column(name = "job_cd", nullable = false, length = 8)
    var jobCd: String? = null, // 직무 코드

    @Column(name = "ocp_cd", nullable = false, length = 8)
    var ocpCd: String? = null, // 직종 코드

    @Column(name = "pt_type_cd", length = 8)
    var ptTypeCd: String? = null, // 환자 유형 코드

    @Column(name = "inst_type_cd", nullable = false, length = 10)
    var instTypeCd: String? = null, // 기관 유형 코드

    @Column(name = "inst_id", nullable = false, length = 10)
    var instId: String? = null, // 기관 ID

    @Column(name = "inst_nm", nullable = false, length = 200)
    var instNm: String? = null, // 기관 이름

    @Column(name = "duty_dstr_1_cd", nullable = false, length = 8)
    var dutyDstr1Cd: String? = null, // 근무 지역 코드 (시도)

    @Column(name = "duty_dstr_2_cd", nullable = false, length = 8)
    var dutyDstr2Cd: String? = null, // 근무 지역 코드 (시군구)

    @Column(name = "duty_addr", length = 100)
    var dutyAddr: String? = null, // 근무지 주소

    @Column(name = "attc_id", length = 10)
    var attcId: String? = null, // 첨부 ID
) : CommonEntity(), java.io.Serializable {
    companion object {
        private const val serialVersionUID: Long = 2139983982022939410L
    }
}