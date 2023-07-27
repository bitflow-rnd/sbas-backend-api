package org.sbas.dtos.info

import com.fasterxml.jackson.annotation.JsonIgnore
import org.sbas.constants.enums.BedStatCd
import org.sbas.constants.enums.NatiCd
import org.sbas.entities.info.InfoPt
import org.sbas.utils.NoArg
import java.time.Instant
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@NoArg
data class InfoPtDto(
    @field: NotBlank val ptNm: String,
    @field: NotBlank val gndr: String,
    @field: NotBlank val rrno1: String,
    @field: NotBlank val rrno2: String,
    @field: NotBlank val dethYn: String?,
    @field: NotNull val natiCd: NatiCd?,
    val natiNm: String?,
    var dstr1Cd: String?,
    var dstr2Cd: String?,
    val telno: String?,
    val picaVer: String?,
    val nokNm: String?,
    val mpno: String?,
    val job: String?,
    val attcId: String?,
    val bascAddr: String,
    val detlAddr: String?,
    val zip: String?,

    val addr: String?,
) {
    fun toEntity(dstr1Cd: String, dstr2Cd: String): InfoPt {
        return InfoPt(
            ptNm = this.ptNm,
            gndr = this.gndr,
            rrno1 = this.rrno1,
            rrno2 = this.rrno2,
            dstr1Cd = dstr1Cd,
            dstr2Cd = dstr2Cd,
            addr = this.addr,
            telno = this.telno,
            natiCd = this.natiCd,
            picaVer = this.picaVer,
            dethYn = this.dethYn,
            nokNm = this.nokNm,
            mpno = this.mpno,
            job = this.job,
            attcId = this.attcId,
            bascAddr = this.bascAddr,
            detlAddr = this.detlAddr,
            zip = this.zip,
            natiNm = if (this.natiCd == NatiCd.NATI0001) {
                "대한민국"
            } else {
                this.natiNm
            },
        )
    }
}

/**
 * 환자 목록 조회용 DTO
 */
data class InfoPtSearchDto(
    var ptId: String?,
    var bdasSeq: Int?,
    var ptNm: String?,
    var gndr: String?,
    var dstr1Cd: String?,
    var dstr1CdNm: String?,
    var dstr2Cd: String?,
    var dstr2CdNm: String?,
    var hospId: String?,
    var hospNm: String?,
    var mpno: String?,
    var natiCd: NatiCd?,
    var bedStatCd: String?,
    var updtDttm: Instant?,
    @JsonIgnore var ptTypeCd: String?,
    @JsonIgnore var svrtTypeCd: String?,
    @JsonIgnore var undrDsesCd: String?,
    var age: Int?,
) {
    var bedStatCdNm: String? = bedStatCd?.let { BedStatCd.valueOf(it).cdNm }
    var tagList: MutableList<String>? = mutableListOf()
}

data class InfoPtBasicInfo(
    val ptId: String?,
    val ptNm: String?,
    val gndr: String?,
    val age: Int?,
    val rrno1: String?,
    val rrno2: String?,
    val bascAddr: String?,
    val detlAddr: String?,
    val dethYn: String?,
    val natiCd: NatiCd?,
    val natiNm: String?,
    val mpno: String?,
    val telno: String?,
    val nokNm: String?,
    val job: String?,
    val attcId: String?,
    val bedStatCd: String?,
    val bedStatNm: String?,
    val undrDsesCd: List<String>?,
)

data class BdasHisInfo(
    val ptId: String?,
    val bdasSeq: Int?,
    val diagNm: String?,
    val hospNm: String?,
    var order: String?,
    val updtDttm: Instant?,
    @JsonIgnore val ptTypeCd: String?,
    @JsonIgnore val svrtTypeCd: String?,
    @JsonIgnore val undrDsesCd: String?,
) {
    var tagList: MutableList<String>? = mutableListOf()
}

@NoArg
data class InfoPtCheckDto(
    var ptId: String,
    var ptNm: String,
    var rrno1: String,
    var rrno2: String,
    var gndr: String? = null,
    var dstr1Cd: String? = null,
    var dstr2Cd: String? = null,
    var telno: String? = null,
    var natiCd: NatiCd? = null,
    var dethYn: String?,
    var nokNm: String? = null,
    var mpno: String? = null,
    var job: String? = null,
    var bascAddr: String? = null,
    var detlAddr: String? = null,
    var zip: String? = null,
    var natiNm: String? = null,
)