package org.sbas.dtos.info

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.ws.rs.QueryParam
import org.jboss.resteasy.reactive.Separator
import org.sbas.constants.enums.*
import org.sbas.entities.info.InfoPt
import org.sbas.utils.annotation.NoArg
import java.time.Instant

data class InfoPtDto(
    @field: NotBlank val ptNm: String,
    @field: NotBlank val gndr: String,
    @field: [NotBlank Pattern(
        regexp = "\\d{2}([0]\\d|[1][0-2])([0][1-9]|[1-2]\\d|[3][0-1])", message = "생년월일을 확인해주세요"
    )]
    val rrno1: String,
    @field: [NotBlank Pattern(
        regexp = "[1-4]|[1-4]\\d{6}", message = "주민등록번호 뒷자리를 확인해주세요"
    )] val rrno2: String,
    @field: NotBlank val dethYn: String,
    @field: NotNull val natiCd: NatiCd,
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
    val undrDsesCd: List<UndrDsesCd>?,
    val undrDsesEtc: String?,

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
                NatiCd.NATI0001.cdNm
            } else {
                this.natiNm
            },
            undrDsesCd = undrDsesCd,
            undrDsesEtc = undrDsesEtc,
        )
    }
}

@NoArg
data class InfoPtSearchParam(
    @field: QueryParam("gndr") var gndr: String?,
    @field: QueryParam("natiCd") var natiCd: NatiCd?,
    @field: QueryParam("dstr1Cd") var dstr1Cd: String?,
    @field: QueryParam("dstr2Cd") var dstr2Cd: String?,
    @field: QueryParam("hospNm") var hospNm: String?,
    @field: QueryParam("bedStatCd") var bedStatCd: String?,
    @field: [QueryParam("dateType") Pattern(
        regexp = "\\b(?:updtDttm|rgstDttm)\\b"
    )]
    var dateType: String?,
    @field: QueryParam("period") var period: Long?,

    @field: QueryParam("ptNm") var ptNm: String?,
    @field: QueryParam("rrno1") var rrno1: String?,
    @field: QueryParam("mpno") var mpno: String?,
    @field: QueryParam("ptId") var ptId: String?,
    @field: QueryParam("page") var page: Int?,
    @field: QueryParam("sever") var sever: Boolean?
)

/**
 * 환자 목록 조회용 DTO
 */
@NoArg
data class InfoPtSearchDto(
    val ptId: String?,
    val bdasSeq: Int?,
    val ptNm: String?,
    val gndr: String?,
    val rrno1: String?,
    val dstr1Cd: String?,
    val dstr1CdNm: String?,
    val dstr2Cd: String?,
    val dstr2CdNm: String?,
    val hospId: String?,
    val hospNm: String?,
    val mpno: String?,
    val natiCd: NatiCd?,
    val natiCdNm: String?,
    val bedStatCd: String?,
    val rgstDttm: Instant?,
    val updtDttm: Instant?,
    val svrtIptTypeCd: String?,
    @JsonIgnore val ptTypeCd: String?,
    val svrtTypeCd: String?,
    @JsonIgnore val undrDsesCd: String?,
    val age: Int?,
    val monitoring: Boolean?,
    val admsDt: String?,
    val covSf: String?,
) {
    val bedStatCdNm: String? = bedStatCd?.let { BedStatCd.valueOf(it).cdNm }
    val tagList: MutableList<String>
        get() {
            val tagList: MutableList<String> = mutableListOf()
            if (ptTypeCd != null) {
                val splitList = ptTypeCd.split(";")
                tagList.addAll(splitList.map { PtTypeCd.valueOf(it).cdNm })
            }
            if (svrtTypeCd != null) {
                val splitList = svrtTypeCd.split(";")
                tagList.addAll(splitList.map { SvrtTypeCd.valueOf(it).cdNm })
            }
            if (undrDsesCd != null) {
                val splitList = undrDsesCd.split(";")
                tagList.addAll(splitList.map { UndrDsesCd.valueOf(it).cdNm })
            }
            return tagList
        }

  val svrtIptTypeCdNm: String? = svrtIptTypeCd?.let { SvrtIptTypeCd.valueOf(it).cdNm }
  val svrtTypeCdNm: String? = svrtTypeCd?.let { SvrtTypeCd.valueOf(it).cdNm }
}

/**
 * 환자 기본 정보
 */
data class InfoPtBasicInfo(
    val ptId: String?,
    val ptNm: String?,
    val gndr: String?,
    val age: Int?,
    val rrno1: String?,
    val rrno2: String?,
    val dstr1Cd: String?,
    val dstr1CdNm: String?,
    val dstr2Cd: String?,
    val dstr2CdNm: String?,
    val bascAddr: String?,
    val detlAddr: String?,
    val zip: String?,
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
    val undrDsesCd: List<UndrDsesCd>?,
    val undrDsesEtc: String?,
    val monitoring: Boolean = false,
) {
    val undrDsesCdNm: List<String>? = undrDsesCd?.map { it.cdNm }
}

/**
 * 환자 병상 배정 이력
 */
data class BdasHisInfo(
    val ptId: String?,
    val bdasSeq: Int?,
    val bedStatCd: String,
    val diagNm: String?,
    val hospNm: String?,
    val updtDttm: Instant?,
    @JsonIgnore val ptTypeCd: String?,
    @JsonIgnore val svrtTypeCd: String?,
    @JsonIgnore val undrDsesCd: String?,
) {
    val tagList: MutableList<String>
        get() {
            val tagList: MutableList<String> = mutableListOf()
            if (ptTypeCd != null) {
                val splitList = ptTypeCd.split(";")
                tagList.addAll(splitList.map { PtTypeCd.valueOf(it).cdNm })
            }
            if (svrtTypeCd != null) {
                val splitList = svrtTypeCd.split(";")
                tagList.addAll(splitList.map { SvrtTypeCd.valueOf(it).cdNm })
            }
            if (undrDsesCd != null) {
                val splitList = undrDsesCd.split(";")
                tagList.addAll(splitList.map { UndrDsesCd.valueOf(it).cdNm })
            }
            return tagList
        }
    var bedStatCdNm: String? = bedStatCd.let { BedStatCd.valueOf(it).cdNm }
    var order: String = ""
}

/**
 * 환자 중복 검사
 */
data class InfoPtCheckRequest(
    val ptNm: String,
    val rrno1: String,
    val rrno2: String,
    val dstr1Cd: String?,
    val dstr2Cd: String?,
    val mpno: String?,
)

data class InfoPtCheckResponse(
    var ptId: String,
    var ptNm: String,
    var rrno1: String,
    var rrno2: String,
    var gndr: String? = null,
    var dstr1Cd: String? = null,
    val dstr1CdNm: String?,
    var dstr2Cd: String? = null,
    val dstr2CdNm: String?,
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

@NoArg
data class BdasHospListRequest(
  @field: QueryParam("dstr1Cd") var dstr1Cd: String?,
  @field: QueryParam("dstr2Cd") var dstr2Cd: String?,
  @field: [QueryParam("bedStatCd") Separator(",")] var bedStatCd: List<String>?,
)