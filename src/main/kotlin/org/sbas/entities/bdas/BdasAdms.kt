package org.sbas.entities.bdas

import org.hibernate.annotations.DynamicUpdate
import org.sbas.dtos.bdas.BdasAdmsSaveDto
import org.sbas.entities.CommonEntity
import org.sbas.utils.StringUtils
import java.io.Serializable
import javax.persistence.*

/**
 * 입원 정보
 */
@DynamicUpdate
@Entity
@Table(name = "bdas_adms")
class BdasAdms(
    @EmbeddedId
    var id: BdasAdmsId? = null,

    @Column(name = "hosp_id", nullable = false, length = 10)
    var hospId: String? = null, // 병원 ID

    @Column(name = "dept_nm", length = 20)
    var deptNm: String? = null, // 진료과 이름

    @Column(name = "ward_nm", length = 20)
    var wardNm: String? = null, // 병동 이름

    @Column(name = "room_nm", length = 20)
    var roomNm: String? = null, // 병실 이름

    @Column(name = "spcl_id", length = 15)
    var spclId: String? = null, // 담당의 ID

    @Column(name = "spcl_nm", length = 10)
    var spclNm: String? = null, // 담당의 이름

    @Column(name = "chrg_telno", length = 12)
    var chrgTelno: String? = null, // 담당 전화번호

    @Column(name = "adms_dt", length = 8)
    var admsDt: String? = null, // 입원 날짜

    @Column(name = "adms_tm", length = 6)
    var admsTm: String? = null, // 입원 시간

    @Column(name = "dsch_dt", length = 8)
    var dschDt: String? = null, // 퇴원 날짜

    @Column(name = "dsch_tm", length = 6)
    var dschTm: String? = null, // 퇴원 시간

    @Column(name = "dsch_rsn_cd", length = 8)
    var dschRsnCd: String? = null, // 퇴원 사유 코드

    @Column(name = "msg", length = 500)
    var msg: String? = null, // 퇴원 사유 상세
    
    @Column(name = "adms_stat_cd", nullable = false)
    var admsStatCd: String? = null // 입퇴원상태 코드
) : CommonEntity() {

    fun changeToAdms(dto: BdasAdmsSaveDto) {
        with(dto) {
            hospId?.let { this@BdasAdms.hospId = it }
            deptNm?.let { this@BdasAdms.deptNm = it }
            wardNm?.let { this@BdasAdms.wardNm = dto.wardNm }
            roomNm?.let { this@BdasAdms.roomNm = dto.roomNm }
            spclId?.let { this@BdasAdms.spclId = dto.spclId }
            spclNm?.let { this@BdasAdms.spclNm = dto.spclNm }
            chrgTelno?.let { this@BdasAdms.chrgTelno = dto.chrgTelno }
            msg?.let { this@BdasAdms.msg = dto.msg }
        }
        this.admsDt = StringUtils.getYyyyMmDd()
        this.admsTm = StringUtils.getHhMmSs()
        admsStatCd = "IOST0001"
    }

    fun changeToDsch(dto: BdasAdmsSaveDto) {
        with(dto) {
            hospId?.let { this@BdasAdms.hospId = it }
            msg?.let { this@BdasAdms.msg = it }
            dschRsnCd?.let { this@BdasAdms.dschRsnCd = it }
        }
        this.dschDt = StringUtils.getYyyyMmDd()
        this.dschTm = StringUtils.getHhMmSs()
        this.admsStatCd = "IOST0002"
    }

    fun changeToHome(dto: BdasAdmsSaveDto) {
        with(dto) {
            hospId?.let { this@BdasAdms.hospId = it }
            msg?.let { this@BdasAdms.msg = it }
        }
        this.admsStatCd = "IOST0003"
    }
}

@Embeddable
data class BdasAdmsId(
    @Column(name = "pt_id", nullable = false, length = 10)
    var ptId: String? = null, // 환자 ID

    @Column(name = "bdas_seq", nullable = false)
    var bdasSeq: Int? = null, // 병상 배정 순번
) : Serializable {

    companion object {
        private const val serialVersionUID = -1724344466522266093L
    }
}


