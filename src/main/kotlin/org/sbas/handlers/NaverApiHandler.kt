package org.sbas.handlers

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.constants.NaverApiConst
import org.sbas.responses.patient.EpidResult
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
    lateinit var serverdomain: String

    @ConfigProperty(name = "upload.path.middle")
    lateinit var uploadRelPath: String

    fun recognizeImage(uri: String, filename: String): EpidResult {
        val dotIdx = filename.lastIndexOf(".")
        val image = OcrApiImagesParam(
            filename.substring(dotIdx + 1),
            "edpireportimg",
            null,
            "$serverdomain/$uri/$filename"
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
        for (field in texts!!) {
            list.add(field.inferText!!)
        }

        return EpidResult(
            rcptPhc = list[1],
            ptNm = list[4],
            rrno1 = list[5].split("-")[0],
            rrno2 = list[5].split("-")[1],
            gndr = list[9],
            dstr1Cd = list[13],
            dstr2Cd = list[14],
            baseAddr = list[13] + " " + list[14] + " " + list[15] + " " + list[16],
            dtlAddr = list[18],
            fullAddr = list[13] + " " + list[14] + " " + list[15] + " " + list[16] + " " + list[17] + " " + list[18],
            telno = list[18],
            diagNm = list[24],
            diagGrde = list[26],
            job = list[28],
            occrDt = list[34],
            diagDt = list[36],
            rptDt = list[38],
            dfdgExamRslt = list[40],
            ptCatg = list[42],
            admsYn = list[44],
            dethYn = list[46],
            rmk = list[50] + " " + list[51],
            instNm = list[53],
            instId = list[55],
            instTelno = list[57],
            instAddr = list[59] + " " + list[60] + " " + list[61] + " " + list[62],
            diagDrNm = list[65],
            rptChfNm = list[67],
        )
    }

}