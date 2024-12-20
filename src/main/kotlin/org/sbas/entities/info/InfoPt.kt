package org.sbas.entities.info

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import org.sbas.constants.UndrDsesCdConverter
import org.sbas.constants.enums.NatiCd
import org.sbas.constants.enums.UndrDsesCd
import org.sbas.dtos.info.InfoPtDto
import org.sbas.entities.CommonEntity
import org.sbas.entities.StringPrefixedSequenceIdGenerator
import java.io.Serial
import java.io.Serializable

/**
 * 환자 기본 정보
 */
@Entity
@Table(name = "info_pt")
class InfoPt(
    @Id
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String, // 환자 ID

    @Column(name = "pt_nm", nullable = false, length = 10)
    var ptNm: String, // 이름

    @Column(name = "gndr", nullable = false, length = 1)
    var gndr: String, // 성별

    @Column(name = "rrno_1", nullable = false, length = 6)
    var rrno1: String, // 주민번호1

    @Column(name = "rrno_2", nullable = false, length = 7)
    var rrno2: String, // 주민번호2

    @Column(name = "dstr_1_cd", length = 8)
    var dstr1Cd: String? = null, // 지역 코드 (시도)

    @Column(name = "dstr_2_cd", length = 8)
    var dstr2Cd: String? = null, // 지역 코드 (시군구)

    @Column(name = "addr", length = 100)
    var addr: String? = null, // 주소

    @Column(name = "telno", length = 12)
    var telno: String? = null, // 전화번호

    @Column(name = "nati_cd", nullable = false, length = 8)
    @Enumerated(EnumType.STRING)
    var natiCd: NatiCd, // 국적 코드

    @Column(name = "pica_ver", length = 10)
    var picaVer: String? = null, // 개인정보수집동의 버전

    @Column(name = "deth_yn", nullable = false, length = 1)
    var dethYn: String, // 사망여부

    @Column(name = "nok_nm", length = 10)
    var nokNm: String? = null, // 보호자 이름

    @Column(name = "mpno", length = 12)
    var mpno: String? = null, // 휴대전화번호

    @Column(name = "job", length = 20)
    var job: String? = null, // 직업

    @Column(name = "attc_id", length = 12)
    var attcId: String? = null, // 첨부 ID

    @Column(name = "basc_addr", nullable = false, length = 100)
    var bascAddr: String, // 기본 주소

    @Column(name = "detl_addr", length = 100)
    var detlAddr: String? = null, // 상세 주소

    @Column(name = "zip", length = 5)
    var zip: String? = null, // 우편번호

    @Column(name = "nati_nm", length = 20)
    var natiNm: String? = null, // 국적 이름

    @Column(name = "undr_dses_cd", nullable = true, length = 512)
    @Convert(converter = UndrDsesCdConverter::class)
    var undrDsesCd: List<UndrDsesCd>? = null, // 기저 질환 코드

    @Column(name = "undr_dses_etc", nullable = true, length = 50)
    var undrDsesEtc: String? = null, // 기저 질환 기타
) : CommonEntity(), Serializable {

    fun updateEntity(infoPtDto: InfoPtDto) {
        this.ptNm = infoPtDto.ptNm
        this.gndr = infoPtDto.gndr
        this.rrno1 = infoPtDto.rrno1
        this.rrno2 = infoPtDto.rrno2
        this.dstr1Cd = infoPtDto.dstr1Cd
        this.dstr2Cd = infoPtDto.dstr2Cd
        this.addr = infoPtDto.addr
        this.telno = infoPtDto.telno
        this.natiCd = infoPtDto.natiCd
        this.picaVer = infoPtDto.picaVer
        this.dethYn = infoPtDto.dethYn
        this.nokNm = infoPtDto.nokNm
        this.mpno = infoPtDto.mpno
        this.job = infoPtDto.job
        this.attcId = infoPtDto.attcId
        this.bascAddr = infoPtDto.bascAddr
        this.detlAddr = infoPtDto.detlAddr
        this.zip = infoPtDto.zip
        this.natiNm = infoPtDto.natiNm
        this.undrDsesCd = infoPtDto.undrDsesCd
        this.undrDsesEtc = infoPtDto.undrDsesEtc
    }

    companion object {
        @Serial
        private const val serialVersionUID: Long = -3263990101530553632L
    }
}