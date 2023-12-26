package org.sbas.responses.patient

import org.sbas.entities.bdas.BdasEsvy
import org.sbas.entities.bdas.BdasReq
import org.sbas.repositories.BaseCodeRepository
import jakarta.inject.Inject

class DiseaseInfoResponse(
    var rcptPhc: String? = null,//담당보건소
    var diagNm: String? = null,//코로나19증상및징후
    var dfdgExamRslt: String? = null,//확진검사결과
    var diagGrde: String? = null,//질병급
    var occrDt: String? = null,//발병일
    var diagDt: String? = null,//진단일
    var rptDt: String? = null,//신고일
    var ptCatg: String? = null,//환자등분류
    var rmk: String? = null,//비고
    var undrDsesNms: List<String?>? = null,//기저질환
    var ptTypeNms: List<String?>? = null,//환자유형
    var svrtTypeNms: List<String?>? = null,//중증도분류
    var bdtp: Float? = null,//체온
    var hr: Int? = null,//맥박
    var resp: Int? = null,//분당호흡수
    var spo2: Int? = null,//산소포화도
    var sbp: Int? = null,//수축기혈압
    var dnrAgreYn: String? = null,//DNR동의
    var admsYn: String? = null,//입원여부
    var instNm: String? = null,//요양기관명
    var instId: String? = null,//요양기관기호
    var instAddr: String? = null,//요양기관주소
    var instTelno: String? = null,//요양기관전화번호
    var diagDrNm: String? = null,//진단의사성명
    var rptChfNm: String? = null,//신고기관장
    var reqBedTypeNm: String? = null,//요청병상유형
) {

    constructor(bdasEsvy: BdasEsvy?, bdasReq: BdasReq?) : this() {
        var arrUndrDsesCd = bdasReq?.undrDsesCd?.split(";")
        var arrPtTypeCd = bdasReq?.ptTypeCd?.split(";")
        var arrSvrtTypeCd = bdasReq?.svrtTypeCd?.split(";")
        rcptPhc=bdasEsvy?.rcptPhc
        diagNm=bdasEsvy?.diagNm
        dfdgExamRslt=bdasEsvy?.dfdgExamRslt
        diagGrde=bdasEsvy?.diagGrde
        occrDt=bdasEsvy?.occrDt
        diagDt=bdasEsvy?.diagDt
        rptDt=bdasEsvy?.rptDt
        ptCatg=bdasEsvy?.ptCatg
        rmk=bdasEsvy?.rmk
        undrDsesNms = arrUndrDsesCd
        ptTypeNms = arrPtTypeCd
        svrtTypeNms = arrSvrtTypeCd
        bdtp=bdasReq?.bdtp
        hr=bdasReq?.hr
        resp=bdasReq?.resp
        spo2=bdasReq?.spo2
        sbp=bdasReq?.sbp
        dnrAgreYn=bdasReq?.dnrAgreYn
        admsYn=bdasEsvy?.admsYn
        instNm=bdasEsvy?.instNm
        instId=bdasEsvy?.instId
        instAddr=bdasEsvy?.instAddr
        instTelno=bdasEsvy?.instTelno
        diagDrNm=bdasEsvy?.diagDrNm
        rptChfNm=bdasEsvy?.rptChfNm
        reqBedTypeNm = bdasReq?.reqBedTypeCd
    }


}