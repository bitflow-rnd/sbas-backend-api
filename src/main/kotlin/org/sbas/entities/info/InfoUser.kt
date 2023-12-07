package org.sbas.entities.info

import org.sbas.constants.enums.*
import org.sbas.dtos.info.InfoUserUpdateReq
import org.sbas.entities.CommonEntity
import java.time.Instant
import javax.persistence.*

/**
 * 사용자 정보
 */
@Entity
@Table(name = "info_user")
class InfoUser(
    @Id
    @Column(name = "user_id", nullable = false, length = 15)
    var id: String,  // 사용자 ID

    @Column(name = "pw", nullable = false, length = 100)
    var pw: String, // 비밀번호

    @Column(name = "user_nm", nullable = false, length = 10)
    var userNm: String, // 사용자 이름

    @Column(name = "user_ci", nullable = false, length = 300)
    var userCi: String? = null, // 사용자 CI

    @Deprecated("user_fcm_token 를 사용하게끔 변경, 삭제예정")
    @Column(name = "push_key", nullable = false, length = 300)
    var pushKey: String? = null, // Push Key

    @Column(name = "gndr", length = 1)
    var gndr: String? = null, //성별

    @Column(name = "telno", nullable = false, length = 11)
    var telno: String, // 전화번호

    @Column(name = "job_cd", nullable = false, length = 8)
    var jobCd: String, // 직무 코드

    @Column(name = "ocp_cd", length = 8)
    var ocpCd: String? = null, // 직종 코드

    @Column(name = "pt_type_cd", length = 250)
    var ptTypeCd: String? = null, // 환자 유형 코드

    @Column(name = "inst_type_cd", nullable = false, length = 10)
    var instTypeCd: String, // 기관 유형 코드

    @Column(name = "inst_id", nullable = false, length = 10)
    var instId: String, // 기관 ID

    @Column(name = "inst_nm", nullable = false, length = 200)
    var instNm: String, // 기관 이름

    @Column(name = "duty_dstr_1_cd", nullable = false, length = 8)
    var dutyDstr1Cd: String, // 근무 지역 코드 (시도)

    @Column(name = "duty_dstr_2_cd", nullable = true, length = 8)
    var dutyDstr2Cd: String?, // 근무 지역 코드 (시군구)

    @Column(name = "attc_id", length = 12)
    var attcId: String? = null, // 첨부 ID

    @Column(name = "user_stat_cd", length = 8)
    @Enumerated(value = EnumType.STRING)
    var userStatCd: UserStatCd? = null, // 상태 구분

    @Column(name = "aprv_user_id", length = 10)
    var aprvUserId: String? = null, // 승인자 ID

    @Column(name = "aprv_dttm")
    var aprvDttm: Instant? = null, // 승인 일시

    @Column(name = "btdt", nullable = false, length = 8)
    var btDt: String, // 생년월일

    @Column(name = "pw_err_cnt")
    var pwErrCnt: Int? = 0, // 비밀번호 오류 횟수

    @Column(name = "auth_cd", nullable = false, length = 8)
    var authCd: String, // 세부권한

) : CommonEntity() {

    fun updateUserStatCdByAdmin(aprvUserId: String, isApproved: Boolean) {
        this.aprvDttm = Instant.now()
        this.aprvUserId = aprvUserId
        this.userStatCd = if (isApproved) {
            UserStatCd.URST0002
        } else {
            UserStatCd.URST0003
        }
    }
    
    // 처음 사용자 등록할 때 사용
    fun setRgstAndUpdtUserIdTo(userId: String?) {
        rgstUserId = userId
        updtUserId = userId
    }

    fun changePasswordTo(pw: String) {
        this.pw = pw
    }

    fun changeTelnoTo(telno: String) {
        this.telno = telno
    }

    fun updateTo(request: InfoUserUpdateReq) {
        this.pw = request.pw
        this.userNm = request.userNm
        this.gndr = request.gndr
        this.telno = request.telno
        this.jobCd = request.jobCd
        this.ocpCd = request.ocpCd
        this.ptTypeCd = request.ptTypeCd
        this.instTypeCd = request.instTypeCd
        this.instId = request.instId
        this.instNm = request.instNm
        this.dutyDstr1Cd = request.dutyDstr1Cd
        this.dutyDstr2Cd = request.dutyDstr2Cd
        this.attcId = request.attcId
        this.btDt = request.btDt
        this.authCd = request.authCd
    }

    fun plusPasswordErrorCount() {
        this.pwErrCnt = pwErrCnt?.plus(1)
    }
}