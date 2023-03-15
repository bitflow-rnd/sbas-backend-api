package org.sbas.entities.info

import org.sbas.entities.CommonEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * 환자 기본 정보
 */
@Entity
@Table(name = "info_pt")
class InfoPt(
    @Id
    @Column(name = "pt_id", nullable = false, length = 10)
    var id: String? = null, // 환자 ID

    @Column(name = "pt_nm", nullable = false, length = 10)
    var ptNm: String? = null, // 이름

    @Column(name = "gndr", nullable = false, length = 1)
    var gndr: String? = null, // 성별

    @Column(name = "rrno_1", nullable = false, length = 6)
    var rrno1: String? = null, // 주민번호1

    @Column(name = "rrno_2", nullable = false, length = 7)
    var rrno2: String? = null, // 주민번호2

    @Column(name = "dstr_1_cd", nullable = false, length = 8)
    var dstr1Cd: String? = null, // 지역 코드 (시도)

    @Column(name = "dstr_2_cd", nullable = false, length = 8)
    var dstr2Cd: String? = null, // 지역 코드 (시군구)

    @Column(name = "addr", nullable = false, length = 100)
    var addr: String? = null, // 주소

    @Column(name = "telno", nullable = false, length = 11)
    var telno: String? = null, // 전화번호

    @Column(name = "nati_cd", nullable = false, length = 8)
    var natiCd: String? = null, // 국적 코드

    @Column(name = "pica_ver", length = 10)
    var picaVer: String? = null, // 개인정보수집동의 버전
) : CommonEntity()