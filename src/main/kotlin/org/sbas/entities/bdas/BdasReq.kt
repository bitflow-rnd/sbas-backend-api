package org.sbas.entities.bdas

import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import org.sbas.entities.CommonEntity
import java.io.Serializable

/**
 * 병상 배정 요청 정보
 */
@DynamicUpdate
@Entity
@Table(name = "bdas_req")
class BdasReq(
    @EmbeddedId
    var id: BdasReqId,

    @Column(name = "req_dt", nullable = false, length = 8)
    var reqDt: String, // 요청 날짜

    @Column(name = "req_tm", nullable = false, length = 6)
    var reqTm: String, // 요청 시간

    @Column(name = "pt_type_cd", nullable = false, length = 256)
    var ptTypeCd: String, // 환자 유형 코드

    @Column(name = "undr_dses_cd", nullable = true, length = 512)
    var undrDsesCd: String? = null, // 기저 질환 코드

    @Column(name = "undr_dses_etc", nullable = true, length = 50)
    var undrDsesEtc: String? = null, // 기저 질환 기타

    @Column(name = "req_bed_type_cd", nullable = false, length = 8)
    var reqBedTypeCd: String, // 요청 병상 유형 코드

    @Column(name = "dnr_agre_yn", nullable = false, length = 8)
    var dnrAgreYn: String, // DNR 동의 여부

    @Column(name = "svrt_ipt_type_cd", nullable = false, length = 8)
    var svrtIptTypeCd: String, // 중증도 입력 형식 코드

    @Column(name = "svrt_type_cd", nullable = false, length = 8)
    var svrtTypeCd: String? = null, // 중증 유형 코드

    @Column(name = "avpu_cd", nullable = true, length = 8)
    var avpuCd: String? = null, // 의식 수준 코드

    @Column(name = "oxy_yn", nullable = true, length = 8)
    var oxyYn: String? = null, // 산소 투여 여부

    @Column(name = "bdtp", nullable = true)
    var bdtp: Float? = null, // 체온

    @Column(name = "hr", nullable = true)
    var hr: Int? = null, // 맥박

    @Column(name = "resp", nullable = true)
    var resp: Int? = null, // 분당 호흡수

    @Column(name = "spo2", nullable = true)
    var spo2: Int? = null, // 산소 포화도

    @Column(name = "sbp", nullable = true)
    var sbp: Int? = null, // 수축기 혈압

    @Column(name = "news_score", nullable = true)
    var newsScore: Int? = null, // NEWS Score

    @Column(name = "req_dstr_1_cd", nullable = false, length = 8)
    var reqDstr1Cd: String, // 요청 지역 코드 (시도)

    @Column(name = "req_dstr_2_cd", nullable = true, length = 8)
    var reqDstr2Cd: String? = null, // 요청 지역 코드 (시군구)

    @Column(name = "dprt_dstr_type_cd", nullable = false, length = 8)
    var dprtDstrTypeCd: String, // 출발 지역 유형 코드

    @Column(name = "dprt_dstr_basc_addr", nullable = true, length = 100)
    var dprtDstrBascAddr: String? = null, // 출발 지역 기본 주소

    @Column(name = "dprt_dstr_detl_addr", nullable = true, length = 100)
    var dprtDstrDetlAddr: String? = null, // 출발 지역 상세 주소

    @Column(name = "dprt_dstr_zip", nullable = true, length = 5)
    var dprtDstrZip: String? = null, // 출발 지역 우편번호

    @Column(name = "dprt_dstr_lat", nullable = true, length = 19)
    var dprtDstrLat: String? = null, // 출발 지역 위도

    @Column(name = "dprt_dstr_lon", nullable = true, length = 19)
    var dprtDstrLon: String? = null, // 출발 지역 경도

    @Column(name = "nok_1_telno", nullable = true, length = 12)
    var nok1Telno: String? = null, // 보호자1 전화번호

    @Column(name = "nok_2_telno", nullable = true, length = 12)
    var nok2Telno: String? = null, // 보호자2 전화번호

    @Column(name = "inhp_asgn_yn", nullable = false, length = 8)
    var inhpAsgnYn: String, // 원내 배정 여부

    @Column(name = "dept_nm", nullable = true, length = 20)
    var deptNm: String? = null, // 진료과 이름

    @Column(name = "spcl_nm", nullable = true, length = 10)
    var spclNm: String? = null, // 담당의 이름

    @Column(name = "chrg_telno", nullable = true, length = 12)
    var chrgTelno: String? = null, // 담당자 전화번호

    @Column(name = "msg", nullable = true, length = 500)
    var msg: String? = null, // 메시지

    @Column(name = "bed_stat_cd", nullable = false, length = 8)
    var bedStatCd: String,
) : CommonEntity() {

    fun changeBedStatTo(bedStatCd: String) {
        this.bedStatCd = bedStatCd
    }

  fun checkBedStatCd(bedStatCd: String): Boolean {
    return this.bedStatCd == bedStatCd
  }
}

@Embeddable
data class BdasReqId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String, // 환자 ID

    @Column(name = "bdas_seq", nullable = false)
    var bdasSeq: Int, // 병상 배정 순번
) : Serializable {

    companion object {
        private const val serialVersionUID = 2691428045062768640L
    }
}