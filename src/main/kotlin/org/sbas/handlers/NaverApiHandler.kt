package org.sbas.handlers

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.constants.NaverApiConst
import org.sbas.responses.patient.EpidResult
import org.sbas.restclients.NaverOcrRestClient
import org.sbas.restparameters.NaverOcrApiParams
import org.sbas.restparameters.OcrApiImagesParam
import java.io.File
import java.io.FileInputStream
import java.util.*
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

        // 로컬 이미지 파일 경로 설정
        val file = File("c:/sbas/www/public/upload/202304/$filename")
        // 이미지 파일 읽어들이기
        val inputStream = FileInputStream(file)
        val bytes = ByteArray(file.length().toInt())
        inputStream.read(bytes)
        // Base64 인코딩
        val encoded: String = Base64.getEncoder().encodeToString(bytes)

        val image = OcrApiImagesParam(
            filename.substring(dotIdx + 1),
            "edpireportimg",
//            encoded,
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

        for ( field in texts!! ) {
            val verticesY = field.boundingPoly!!.vertices!![0].y!!
            if (verticesY < 14.0) {
                continue
            }
            list.add(field.inferText!!)
        }

//        val iterator = list.listIterator()
//
//        while (iterator.hasNext()) {
//            val value = iterator.next()
//            if (value == "보호자성명" && iterator.hasNext() && iterator.next() == "등록번호") {
//                iterator.previous()
//                iterator.add("")
//            }
//            if (value == "전화번호" && iterator.hasNext() && iterator.next() == "주소") {
//                iterator.previous()
//                iterator.add("")
//            }
//        }

//        return list
//        return texts
        //TODO 값이 없는 항목 처리
        return EpidResult(
            rcptPhc = list[1],
            ptNm = list[4],
//            rrno1 = list[5],
//            rrno2 = list[5],
            rrno1 = list[5].split("-")[0],
            rrno2 = list[5].split("-")[1],
            gndr = list[9],
            dstr1Cd = list[13],
            dstr2Cd = list[14],
            baseAddr = list[13] + " " + list[14] + " " + list[15] + " " + list[16],
            dtlAddr = list[18],
            fullAddr = list[13] + " " + list[14] + " " + list[15] + " " + list[16] + " " + list[17] + " " + list[18],
            mpno = list[21].replace("-", ""),
            diagNm = list[24],
            diagGrde = list[26],
            job = list[28],
            cv19Symp = list[31],
            occrDt = list[34],
            diagDt = list[36],
            rptDt = list[38],
            dfdgExamRslt = list[40],
            ptCatg = list[42],
            admsYn = list[44],
            dethYn = list[46],
            rptType = list[48],
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