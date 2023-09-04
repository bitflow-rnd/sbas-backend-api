package org.sbas.dtos.info

import org.sbas.constants.enums.UserStatCd
import org.sbas.entities.info.InfoUser
import org.sbas.utils.annotation.NoArg
import java.time.Instant
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
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
)

/**
 * 사용자 등록 DTO
 */
data class InfoUserSaveRequest(
    @field: [NotBlank Pattern(regexp = "^[a-zA-Z0-9@._-]+\$", message = "영소문자, 영대문자, 숫자, @.-_ 만 가능합니다")]
    val id: String,
    @field: NotBlank
    val pw: String?,
    val userNm: String?,
    val userCi: String?,
    val pushKey: String?,
    @field: NotBlank
    val telno: String?,
    val jobCd: String?,
    val ocpCd: String?,
    val ptTypeCd: String?,
    val instTypeCd: String?,
    val instId: String?,
    val instNm: String?,
    val dutyDstr1Cd: String?,
    val dutyDstr2Cd: String?,
    val attcId: String?,
    val btDt: String,
    val authCd: String,
    var userStatCd: UserStatCd?,
) {
    fun toEntity(userStatCd: UserStatCd?): InfoUser {
        return InfoUser(
            id = id,
            pw = pw,
            userNm = userNm,
            userCi = userCi ?: "",
            pushKey = pushKey ?: "",
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