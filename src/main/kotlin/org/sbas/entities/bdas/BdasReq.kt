package org.sbas.entities.bdas

import org.sbas.entities.CommonEntity
import java.io.Serializable
import javax.persistence.*

/**
 * 병상 배정 요청 정보
 */
@Entity
@Table(name = "bdas_req")
class BdasReq(
    @EmbeddedId
    var id: BdasReqId? = null,

    @Column(name = "hist_cd", nullable = false, length = 8)
    var histCd: String? = null,

    @Column(name = "req_dt", nullable = false, length = 8)
    var reqDt: String? = null,

    @Column(name = "req_tm", nullable = false, length = 6)
    var reqTm: String? = null,

    @Column(name = "pt_type_cd", nullable = false, length = 8)
    var ptTypeCd: String? = null,

    @Column(name = "undr_dses_cd", nullable = true, length = 8)
    var undrDsesCd: String? = null,

    @Column(name = "undr_dses_etc", nullable = true, length = 50)
    var undrDsesEtc: String? = null,

    @Column(name = "req_bed_type_cd", nullable = false, length = 8)
    var reqBedTypeCd: String? = null,

    @Column(name = "dnr_agre_yn", nullable = false, length = -1)
    var dnrAgreYn: String? = null,

    @Column(name = "svrt_ipt_type_cd", nullable = false, length = 8)
    var svrtIptTypeCd: String? = null,

    @Column(name = "svrt_type_cd", nullable = false, length = 8)
    var svrtTypeCd: String? = null,

    @Column(name = "avpu_cd", nullable = true, length = 8)
    var avpuCd: String? = null,

    @Column(name = "oxy_yn", nullable = true, length = 8)
    var oxyYn: String? = null,

    @Column(name = "bdtp", nullable = true)
    var bdtp: Int? = null,

    @Column(name = "hr", nullable = true)
    var hr: Int? = null,

    @Column(name = "resp", nullable = true)
    var resp: Int? = null,

    @Column(name = "spo2", nullable = true)
    var spo2: Int? = null,

    @Column(name = "sbp", nullable = true)
    var sbp: Int? = null,

    @Column(name = "svrt_teyp_cd_anly", nullable = true, length = 8)
    var svrtTeypCdAnly: String? = null,

    @Column(name = "news_score", nullable = true)
    var newsScore: Int? = null,

    @Column(name = "who_score", nullable = true)
    var whoScore: Int? = null,

    @Column(name = "req_type_cd", nullable = false, length = 8)
    var reqTypeCd: String? = null,

    @Column(name = "req_dstr_1_cd", nullable = false, length = 8)
    var reqDstr1Cd: String? = null,

    @Column(name = "req_dstr_2_cd", nullable = true, length = 8)
    var reqDstr2Cd: String? = null,

    @Column(name = "dprt_dstr_type_cd", nullable = false, length = 8)
    var dprtDstrTypeCd: String? = null,

    @Column(name = "dprt_dstr_basc_addr", nullable = true, length = 100)
    var dprtDstrBascAddr: String? = null,

    @Column(name = "dprt_dstr_detl_addr", nullable = true, length = 100)
    var dprtDstrDetlAddr: String? = null,

    @Column(name = "dprt_dstr_zip", nullable = true, length = 5)
    var dprtDstrZip: String? = null,

    @Column(name = "dprt_dstr_lat", nullable = true, length = 19)
    var dprtDstrLat: String? = null,

    @Column(name = "dprt_dstr_lon", nullable = true, length = 19)
    var dprtDstrLon: String? = null,

    @Column(name = "nok_1_telno", nullable = true, length = 12)
    var nok1Telno: String? = null,

    @Column(name = "nok_2_telno", nullable = true, length = 12)
    var nok2Telno: String? = null,

    @Column(name = "dprt_hosp_id", nullable = true, length = 10)
    var dprtHospId: String? = null,

    @Column(name = "inhp_asgn_yn", nullable = false, length = 8)
    var inhpAsgnYn: String? = null,

    @Column(name = "dept_nm", nullable = true, length = 20)
    var deptNm: String? = null,

    @Column(name = "spcl_nm", nullable = true, length = 10)
    var spclNm: String? = null,

    @Column(name = "chrg_telno", nullable = true, length = 12)
    var chrgTelno: String? = null,

    @Column(name = "msg", nullable = true, length = 500)
    var msg: String? = null,
) : CommonEntity()

@Embeddable
data class BdasReqId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null, // 환자 ID

    @Column(name = "bdas_seq", nullable = false)
    var bdasSeq: Int? = null, // 병상 배정 순번

    @Column(name = "hist_seq", nullable = false)
    var histSeq: Int? = null,
) : Serializable {

    companion object {
        private const val serialVersionUID = 2691428045062768640L
    }
}