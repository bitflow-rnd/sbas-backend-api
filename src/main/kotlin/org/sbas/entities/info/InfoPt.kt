package org.sbas.entities.info

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import org.sbas.entities.CommonEntity
import org.sbas.entities.StringPrefixedSequenceIdGenerator
import javax.persistence.*

/**
 * 환자 기본 정보
 */
@Entity
@Table(name = "info_pt")
class InfoPt(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pt_seq")
    @GenericGenerator(
        name = "pt_seq",
        strategy = "org.sbas.entities.StringPrefixedSequenceIdGenerator",
        parameters = [
            Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "PT"),
            Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%08d"),
            Parameter(name = StringPrefixedSequenceIdGenerator.incrementSize, value = "1")
        ]) // PT00000000
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

data class InfoPtSaveReq(
    val ptNm: String?,
    val gndr: String?,
    val rrno1: String?,
    val rrno2: String?,
    val dstr1Cd: String?,
    val dstr2Cd: String?,
    val addr: String?,
    val telno: String?,
    val natiCd: String?,
)

fun InfoPtSaveReq.toEntity(): InfoPt {
    return InfoPt(
        ptNm = this.ptNm,
        gndr = this.gndr,
        rrno1 = this.rrno1,
        rrno2 = this.rrno2,
        dstr1Cd = this.dstr1Cd,
        dstr2Cd = this.dstr2Cd,
        addr = this.addr,
        telno = this.telno,
        natiCd = this.natiCd,
    )
}