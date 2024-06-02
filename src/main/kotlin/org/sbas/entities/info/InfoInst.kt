package org.sbas.entities.info

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.sbas.dtos.info.InfoInstUpdateReq
import org.sbas.entities.CommonEntity

/**
 * 기관 정보
 */
@Entity
@Table(name = "info_inst")
class InfoInst(
    @Id
    @Column(name = "inst_id", nullable = false, length = 10)
    var id: String, // 기관 ID

    @Column(name = "inst_type_cd", nullable = false, length = 8)
    var instTypeCd: String, // 기관 유형 코드

    @Column(name = "inst_nm", nullable = false, length = 100)
    var instNm: String, // 기관 이름

    @Column(name = "dstr_1_cd", nullable = false, length = 8)
    var dstr1Cd: String, // 지역 코드 (시도)

    @Column(name = "dstr_2_cd", length = 8)
    var dstr2Cd: String? = null, // 지역 코드 (시군구)

    @Column(name = "chrg_id", length = 10)
    var chrgId: String? = null, // 담당자 ID

    @Column(name = "chrg_nm", length = 10)
    var chrgNm: String? = null, // 담당자 이름

    @Column(name = "chrg_telno", length = 12)
    var chrgTelno: String? = null, // 담당자 전화번호

    @Column(name = "detl_addr")
    var detlAddr: String? = null, // 상세 주소

    @Column(name = "lat")
    var lat: String? = null, // 위도

    @Column(name = "lon")
    var lon: String? = null, // 경도

    @Column(name = "rmk", length = 200)
    var rmk: String? = null, // 비고

    @Column(name = "attc_id", length = 10)
    var attcId: String? = null, // 첨부 ID

    @Column(name = "vecno", length = 50)
    var vecno: String? = null, // 차량번호
) : CommonEntity() {

    fun update(updateDto: InfoInstUpdateReq) {
        with(updateDto) {
            instNm.let { this@InfoInst.instNm = it }
            dstr1Cd.let { this@InfoInst.dstr1Cd = it }
            dstr2Cd?.let { this@InfoInst.dstr2Cd = it }
            chrgId?.let { this@InfoInst.chrgId = it }
            chrgNm?.let { this@InfoInst.chrgNm = it }
            chrgTelno?.let { this@InfoInst.chrgTelno = it }
            baseAddr?.let { this@InfoInst.detlAddr = it }
            lat?.let { this@InfoInst.lat = it }
            lon?.let { this@InfoInst.lon = it }
            rmk?.let { this@InfoInst.rmk = it }
            attcId?.let { this@InfoInst.attcId = it }
        }
    }

    fun updateFireStatnVecno(vecno: String?) {
        if (this.vecno.isNullOrEmpty()) {
            this.vecno = vecno
        } else {
            this.vecno += ";$vecno"
        }
    }
}