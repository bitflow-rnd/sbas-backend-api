package org.sbas.responses.patient

import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.sbas.utils.NoArg

@NoArg
data class EpidResult (

    // idx: 1
    @Schema(description = "수신보건소", example = "대구광역시수성구보건소")
    var rcptPhc: String?,

    // idx: 4
    @Schema(description = "이름", example = "홍길동")
    var ptNm: String?,

    // idx: 5 split(0)
    @Schema(description = "주민번호1", example = "950101")
    var rrno1: String?,

    // idx: 5 split(1)
    @Schema(description = "주민번호2", example = "1")
    var rrno2: String?,

    // idx: 9
    @Schema(description = "성별", example = "남")
    var gndr: String?,

    // idx: 13
    @Schema(description = "지역코드(시/도)", example = "11")
    var dstr1Cd: String?,

    // idx: 14
    @Schema(description = "지역코드(시/군/구)", example = "110312")
    var dstr2Cd: String?,

    // idx: 13~16
    @Schema(description = "기본주소", example = "솔샘로25길 28")
    var baseAddr: String?,

    // idx: 18
    @Schema(description = "상세주소", example = "솔샘로25길 28")
    var dtlAddr: String?,

    // idx: 13~18
    @Schema(description = "전체주소", example = "서울특별시 성북구 솔샘로25길 28 (정릉동 정릉풍림아이원) 105-202")
    var fullAddr: String?,

    // idx: 21
    @Schema(description = "휴대전화번호", example = "솔샘로25길 28")
    var telno: String?,

    // idx: 24
    @Schema(description = "질병이름", example = "코로나바이러스감염증-19")
    var diagNm: String?,

    // idx: 26
    @Schema(description = "질병급", example = "2급")
    var diagGrde: String?,

    // idx: 28
    @Schema(description = "직업", example = "기타(기타)")
    var job: String?,

    // idx: 32
    @Schema(description = "코로나19 증상 및 징후", example = "열")
    var cv19Symp: String?,

    // idx: 34
    @Schema(description = "발병일", example = "2023-03-20")
    var occrDt: String?,

    // idx: 36
    @Schema(description = "진단일", example = "2023-03-20")
    var diagDt: String?,

    // idx: 38
    @Schema(description = "신고일", example = "2023-03-20")
    var rptDt: String?,

    // idx: 40
    @Schema(description = "확진검사결과", example = "양성")
    var dfdgExamRslt: String?,

    // idx: 42
    @Schema(description = "환자등분류", example = "환자")
    var ptCatg: String?,

    // idx: 44
    @Schema(description = "입원여부", example = "외래")
    var admsYn: String?,

    // idx: 46
    @Schema(description = "사망여부", example = "생존")
    var dethYn: String?,

    // idx: 48
    @Schema(description = "신고구분", example = "자동 신고")
    var rptType: String?,

    // idx: 50~51
    @Schema(description = "비고", example = "전문가용 RAT")
    var rmk: String?,

    // idx: 53
    @Schema(description = "요양기관명", example = "김이비인후과의원")
    var instNm: String?,

    // idx: 55
    @Schema(description = "요양기관 기호", example = "99321552")
    var instId: String?,

    // idx: 57
    @Schema(description = "요양기관 전화번호", example = "053-123-4567")
    var instTelno: String?,

    // idx: 59~62
    @Schema(description = "요양기관 주소", example = "대구광역시 수성구 김수로 100")
    var instAddr: String?,

    // idx: 65
    @Schema(description = "진단의사 성명", example = "홍길동")
    var diagDrNm: String?,

    // idx: 67
    @Schema(description = "신고기관장 성명", example = "홍길동")
    var rptChfNm: String?
)

/*
수신보건소|대구광역시수성구보건소|주민(외국인)|성명(사망자)|정예준|100817-3|보호자성명|등록번호|성별|남
|연령(만)|12세|전화번호|서울특별시|성북구|솔샘로25길|28|(정릉동 정릉풍림아이원)|105-202|주소
|휴대 전화번호|010-5068-0725|0|질병명|코로나바이러스감염증-19|질병급|2급|직업|기타(기타)|코로나바이러스감염
|증-19|열|증상 및 징후|발병일|2023-03-20|진단일|2023-03-20|신고일|2023-03-20|확진검사결과
|양성|환자등분류|환자|입원여부|외래|사망여부|생존|신고구분|자동 신고|비고
|전문가용|RAT|요양기관명|박이비인후과의원|요양기관기호|37321552|전화번호|053-764-8924|요양기관주소|대구광역시
|수성구|청수로|200|진단의사|성명|박형태|신고기관장 성명|박형태
 */