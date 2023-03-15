package org.sbas.entities.bdas

import org.sbas.entities.CommonEntity
import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.*

/**
 * 병상 배정 요청 정보
 */
@Entity
@Table(name = "bdas_req")
class BdasReq(
    @EmbeddedId
    var id: BdasReqId? = null,

    @Column(name = "req_dt", length = 8)
    var reqDt: String? = null, // 요청 날짜

    @Column(name = "req_tm", length = 6)
    var reqTm: String? = null, // 요청 시간

    @Column(name = "req_type_cd", length = 8)
    var reqTypeCd: String? = null, // 요청 유형 코드

    @Column(name = "req_hosp_id", length = 10)
    var reqHospId: String? = null, // 요청 병원 ID

    @Column(name = "req_user_id", length = 10)
    var reqUserId: String? = null, // 요청 사용자 ID

    @Column(name = "pt_loc_addr", length = 100)
    var ptLocAddr: String? = null, // 환자 위치 주소

    @Column(name = "pt_loc_lat", length = 20)
    var ptLocLat: String? = null, // 환자 위치 (위도)

    @Column(name = "pt_loc_lon", length = 20)
    var ptLocLon: String? = null, // 환자 위치 (경도)

    @Column(name = "adms_yn", length = 1)
    var admsYn: String? = null, // 입원 여부

    @Column(name = "clnc_yn", length = 1)
    var clncYn: String? = null, // 임상 여부

    @Column(name = "nok_nm", length = 10)
    var nokNm: String? = null, // 보호자 이름

    @Column(name = "nok_telno", length = 11)
    var nokTelno: String? = null, // 보호자 전화번호

    @Column(name = "adms_hosp_id", length = 10)
    var admsHospId: String? = null, // 입원 병원 ID

    @Column(name = "adms_dept_nm", length = 20)
    var admsDeptNm: String? = null, // 입원 진료과 이름

    @Column(name = "adms_spcl_nm", length = 10)
    var admsSpclNm: String? = null, // 입원 담당의 이름

    @Column(name = "adms_spcl_telno", length = 11)
    var admsSpclTelno: String? = null, // 입원 담당의 전화번호

    @Column(name = "pt_type_cd", length = 8)
    var ptTypeCd: String? = null, // 환자 유형 코드

    @Column(name = "diag_cd", length = 8)
    var diagCd: String? = null, // 진단 코드

    @Column(name = "diag_nm_hng", length = 100)
    var diagNmHng: String? = null, // 진단명 (한글)

    @Column(name = "diag_nm_eng", length = 100)
    var diagNmEng: String? = null, // 진단명 (영문)

    @Column(name = "svrt_type_cd", length = 8)
    var svrtTypeCd: String? = null, // 중증 유형 코드

    @Column(name = "news_score_curr", precision = 1)
    var newsScoreCurr: BigDecimal? = null, // NEWS 점수 (현재)

    @Column(name = "news_score_prdt", precision = 1)
    var newsScorePrdt: BigDecimal? = null, // NEWS 점수 (예측)

    @Column(name = "who_score_curr", precision = 1)
    var whoScoreCurr: BigDecimal? = null, // WHO 점수 (현재)

    @Column(name = "who_score_prdt", precision = 1)
    var whoScorePrdt: BigDecimal? = null, // WHO 점수 (예측)

    @Column(name = "sbed_cd_curr", length = 8)
    var sbedCdCurr: String? = null, // 중증병상 코드 (현재)

    @Column(name = "sbed_cd_prdt", length = 8)
    var sbedCdPrdt: String? = null, // 중증병상 코드 (예측)

    @Column(name = "\"desc\"", length = 4000)
    var desc: String? = null, // 설명

    @Column(name = "attc_id", length = 10)
    var attcId: String? = null, // 첨부 ID
) : CommonEntity()

@Embeddable
data class BdasReqId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null, // 환자 ID

    @Column(name = "bdas_seq", nullable = false, precision = 10)
    var bdasSeq: BigDecimal? = null, // 병상 배정 순번
) : Serializable {

    companion object {
        private const val serialVersionUID = 2691428045062768640L
    }
}