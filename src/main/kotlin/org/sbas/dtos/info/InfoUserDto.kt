package org.sbas.dtos.info

import org.sbas.constants.UserStatCd
import org.sbas.entities.info.InfoUser
import org.sbas.utils.NoArg
import java.time.Instant
import javax.ws.rs.QueryParam

/**
 * 사용자 목록 조회 DTO
 */
data class InfoUserListDto(
    var userId: String,
    var dutyDstr1Cd: String?,
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
    @field:QueryParam("dstr1Cd") var dstr1Cd: String?,
    @field:QueryParam("dstr2Cd") var dstr2Cd: String?,
    @field:QueryParam("userNm") var userNm: String?,
    @field:QueryParam("telno") var telno: String?,
    @field:QueryParam("instNm") var instNm: String?,
    @field:QueryParam("instTypeCd") var instTypeCd: String?,
    @field:QueryParam("ptTypeCd") var ptTypeCd: String?,
    @field:QueryParam("userStatCd") var userStatCd: UserStatCd?,
)

/**
 * 사용자 등록 DTO
 */
@NoArg
data class InfoUserSaveDto(
    var userId: String,
    var pw: String?,
    var userNm: String?,
    var telno: String?,
    var jobCd: String?,
    var ocpCd: String?,
    var ptTypeCd: String?,
    var instTypeCd: String?,
    var instId: String?,
    var instNm: String?,
    var dutyDstr1Cd: String?,
    var dutyDstr2Cd: String?,
    var attcId: String?,
    var btDt: String,
    var authCd: String,
    var userStatCd: UserStatCd = UserStatCd.URST0001,
) {
    fun toEntity(): InfoUser {
        return InfoUser(
            id = userId,
            pw = pw,
            userNm = userNm,
            userCi = "",
            pushKey = "",
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