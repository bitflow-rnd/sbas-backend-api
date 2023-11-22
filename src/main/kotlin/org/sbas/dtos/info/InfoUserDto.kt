package org.sbas.dtos.info

import org.sbas.constants.enums.*
import org.sbas.entities.info.InfoUser
import org.sbas.utils.annotation.NoArg
import org.sbas.utils.annotation.ValidEnum
import java.time.Instant
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.ws.rs.QueryParam

/**
 * 사용자 목록 조회 DTO
 */
data class InfoUserListDto(
    var userId: String,
    var dutyDstr1Cd: String?,
    val dutyDstr1CdNm: String?,
    var instTypeCd: String?,
    var instNm: String?,
    var userNm: String?,
    var jobCd: String?,
    var authCd: String?,
    var rgstDttm: Instant,
    var userStatCd: UserStatCd?,
    var userStatCdNm: String?,
)

/**
 * 사용자 목록 검색 parameters
 */
@NoArg
data class InfoUserSearchParam(
    @field: QueryParam("dstr1Cd") var dstr1Cd: String?,
    @field: QueryParam("dstr2Cd") var dstr2Cd: String?,
    @field: QueryParam("userNm") var userNm: String?,
    @field: QueryParam("telno") var telno: String?,
    @field: QueryParam("instNm") var instNm: String?,
    @field: QueryParam("instTypeCd") var instTypeCd: String?,
    @field: QueryParam("ptTypeCd") var ptTypeCd: String?,
    @field: QueryParam("userStatCd") var userStatCd: UserStatCd?,
    @field: QueryParam("page") var page: Int?,
    @field: QueryParam("userStatCdStr") var userStatCdStr: String?
) {
}

/**
 * 사용자 상세 정보
 */
data class UserDetailResponse(
    val id: String,
    val userNm: String,
    val gndr: String?,
    val telno: String,
    val jobCd: String,
    val ocpCd: String?,
    val ptTypeCd: String?,
    val instTypeCd: String,
    val instId: String,
    val instNm: String,
    val dutyDstr1Cd: String,
    val dutyDstr1CdNm: String,
    val dutyDstr2Cd: String,
    val dutyDstr2CdNm: String,
    val btDt: String,
    val authCd: String,
    val attcId: String?,
    val userStatCd: UserStatCd?,
    val updtDttm: Instant,
) {
    val jobCdNm = PmgrTypeCd.valueOf(jobCd).cdNm
    val authCdNm = DtpmTypeCd.valueOf(authCd).cdNm
    val instTypeCdNm = InstTypeCd.valueOf(instTypeCd).cdNm
    val ptTypeCdNm: List<String>?
        get() {
            val list = ptTypeCd?.split(";")
            return list?.map { PtTypeCd.valueOf(it).cdNm }
        }
}

/**
 * 사용자 등록 DTO
 */
data class InfoUserSaveReq(
    @field: [NotBlank
    Pattern(regexp = "^[a-zA-Z0-9@._-]+\$",
        message = "영소문자, 영대문자, 숫자, @.-_ 만 가능합니다")
    Size(min = 1, max = 15)]
    val id: String,

    @field: [NotBlank Size(min = 1, max = 256)]
    val pw: String,

    @field: [NotBlank Size(min = 1, max = 10)]
    val userNm: String,

    @field: [Size(min = 1, max = 1) Pattern(regexp = "^(남|여)\$")]
    val gndr: String?,

    @field: [NotBlank
    Pattern(regexp = "^(01[016789]{1})[0-9]{3,4}[0-9]{4}\$",
        message = "휴대전화 번호를 확인해 주세요.")
    Size(min = 1, max = 12)]
    val telno: String,

    @field: [Size(min = 1, max = 8) NotBlank ValidEnum(enumClass = PmgrTypeCd::class)]
    val jobCd: String,

    @field: Size(max = 8)
    val ocpCd: String?,

    @field: ValidEnum(enumClass = PtTypeCd::class, isNullable = true)
    val ptTypeCd: String?,

    @field: [NotBlank Size(min = 1, max = 8) ValidEnum(enumClass = InstTypeCd::class)]
    val instTypeCd: String,

    @field: [NotBlank Size(min = 1, max = 10)]
    val instId: String,

    @field: [NotBlank Size(min = 1, max = 200)]
    val instNm: String,

    @field: [NotBlank Size(min = 1, max = 8)]
    val dutyDstr1Cd: String,

    @field: [NotBlank Size(min = 1, max = 8)]
    val dutyDstr2Cd: String,

    @field: Size(max = 12)
    val attcId: String?,

    @field: [NotBlank
    Pattern(regexp = "^(19[0-9][0-9]|20\\d{2})(0[0-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])\$",
        message = "생년월일을 확인해 주세요.")
    Size(min = 1, max = 8)]
    val btDt: String,

    @field: [NotBlank Size(min = 1, max = 8) ValidEnum(enumClass = DtpmTypeCd::class)]
    val authCd: String,
) {
    fun toEntity(userStatCd: UserStatCd?): InfoUser {
        val ptTypeCd = ptTypeCd?.let { it.ifEmpty { null } }
        return InfoUser(
            id = id,
            pw = pw,
            userNm = userNm,
            userCi = "",
            pushKey = "",
            gndr = gndr,
            telno = telno,
            jobCd = jobCd,
            ocpCd = ocpCd,
            ptTypeCd = ptTypeCd,
            instTypeCd = instTypeCd,
            instId = instId,
            instNm = instNm,
            dutyDstr1Cd = dutyDstr1Cd,
            dutyDstr2Cd = dutyDstr2Cd,
            attcId = attcId,
            userStatCd = userStatCd,
            btDt = btDt,
            authCd = authCd,
        )
    }
}

data class InfoUserUpdateReq(
    val id: String,

    @field: [NotBlank Size(min = 1, max = 256)]
    val pw: String,

    @field: [NotBlank Size(min = 1, max = 10)]
    val userNm: String,

    @field: [Size(min = 1, max = 1) Pattern(regexp = "^(남|여)\$")]
    val gndr: String?,

    @field: [NotBlank Size(min = 1, max = 12)]
    val telno: String,

    @field: [Size(min = 1, max = 8) NotBlank ValidEnum(enumClass = PmgrTypeCd::class)]
    val jobCd: String,

    @field: Size(min = 1, max = 8)
    val ocpCd: String?,

    @field: [Size(min = 1) ValidEnum(enumClass = PtTypeCd::class, isNullable = true)]
    val ptTypeCd: String?,

    @field: [NotBlank Size(min = 1, max = 8) ValidEnum(enumClass = InstTypeCd::class)]
    val instTypeCd: String,

    @field: [NotBlank Size(min = 1, max = 10)]
    val instId: String,

    @field: [NotBlank Size(min = 1, max = 200)]
    val instNm: String,

    @field: [NotBlank Size(min = 1, max = 8)]
    val dutyDstr1Cd: String,

    @field: [NotBlank Size(min = 1, max = 8)]
    val dutyDstr2Cd: String,

    @field: [Size(min = 1, max = 12)]
    val attcId: String?,

    @field: [NotBlank Size(min = 1, max = 8)]
    val btDt: String,

    @field: [NotBlank Size(min = 1, max = 8) ValidEnum(enumClass = DtpmTypeCd::class)]
    val authCd: String,
)
