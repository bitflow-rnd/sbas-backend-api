package org.sbas.handlers

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.constants.NaverApiConst
import org.sbas.response.patient.EpidResult
import org.sbas.restclients.NaverOcrRestClient
import org.sbas.restparameters.NaverOcrApiParams
import org.sbas.restparameters.OcrApiImagesParam
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class NaverApiHandler {

    @Inject
    lateinit var log: Logger

    @RestClient
    lateinit var naverOcrClient: NaverOcrRestClient

    @ConfigProperty(name = "restclient.naverocr.secret.key")
    lateinit var secretkey: String

    @ConfigProperty(name = "domain.this")
    lateinit var serverDomain: String

    @ConfigProperty(name = "upload.path.relative")
    lateinit var uploadRelPath: String


    fun recognizeImage(param: String): EpidResult {
        val dotIdx = param.lastIndexOf(".")
        val image = OcrApiImagesParam(
            param.substring(dotIdx + 1),
            "edpireportimg",
            null,
            "$serverDomain/$uploadRelPath/$param"
        )
        val now = System.currentTimeMillis()
        val images = mutableListOf<OcrApiImagesParam>()
        images.add(image)
        val reqparam = NaverOcrApiParams(
            images,
            NaverApiConst.ClovaOcr.VERSION,
            now.toString(),
            now,
            NaverApiConst.ClovaOcr.LANG
        )
        val res = naverOcrClient.recognize(reqparam)
        val texts = res.images[0].fields
        val list = ArrayList<String>()
        val ret = EpidResult()
        for (field in texts!!) {
            list.add(field.inferText!!)
        }
        ret.rcptPhc = list[1]
        ret.ptNm = list[4]
        ret.rrno1 = list[5].split("-")[0]
        ret.rrno2 = list[5].split("-")[1]
        ret.gndr = list[9]
        ret.dstr1Cd = list[13]
        ret.dstr2Cd = list[14]
        ret.baseAddr = list[13] + " " + list[14] + " " + list[15] + " " + list[16]
        ret.dtlAddr = list[18]
        ret.fullAddr = list[13] + " " + list[14] + " " + list[15] + " " + list[16] + " " + list[17] + " " + list[18]
        ret.rcptPhc = list[1]
        ret.telno = list[18]
        ret.diagNm = list[24]
        ret.diagGrde = list[26]
        ret.job = list[28]
        ret.occrDt = list[34]
        ret.diagDt = list[36]
        ret.rptDt = list[38]
        ret.dfdgExamRslt = list[40]
        ret.ptCatg = list[42]
        ret.admsYn = list[44]
        ret.dethYn = list[46]
        ret.rmk = list[50] + list[51]
        ret.instNm = list[53]
        ret.instId = list[55]
        ret.instTelno = list[57]
        ret.instAddr = list[59] + " " + list[60] + " " + list[61] + " " + list[62]
        ret.diagDrNm = list[65]
        ret.rptChfNm = list[67]
        return ret
    }

}