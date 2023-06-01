package org.sbas.handlers

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.constants.NaverApiConst
import org.sbas.responses.patient.EpidResult
import org.sbas.restclients.NaverOcrRestClient
import org.sbas.restparameters.NaverOcrApiParams
import org.sbas.restparameters.OcrApiImagesParam
import org.sbas.utils.StringUtils
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

        /* 로컬 테스트용 코드
        // 로컬 이미지 파일 경로 설정
        val file = File("c:/sbas/www/public/upload/202304/$filename")
        // 이미지 파일 읽어들이기
        val inputStream = FileInputStream(file)
        val bytes = ByteArray(file.length().toInt())
        inputStream.read(bytes)
        // Base64 인코딩
        val encoded: String = Base64.getEncoder().encodeToString(bytes)
        */

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
            list.add(field.inferText!!)
        }

        val address = list[6]

        return EpidResult(
            rcptPhc = list[0],
            ptNm = list[1],
            rrno1 = list[2].split("-")[0],
            rrno2 = list[2].split("-")[1],
            nokNm = list[3],
            gndr = list[4],
            telno = list[5].replace("-", ""),
            dstr1Cd = splitAddress(address)[0],
            dstr2Cd = splitAddress(address)[1],
            baseAddr = splitAddress(address)[2],
            dtlAddr = splitAddress(address)[3],
            fullAddr = splitAddress(address)[4],
            mpno = list[7].replace("-", ""),
            diagNm = list[8],
            diagGrde = list[9],
            job = list[10],
            cv19Symp = list[11],
            occrDt = list[12],
            diagDt = list[13],
            rptDt = list[14],
            dfdgExamRslt = list[15],
            ptCatg = list[16],
            admsYn = list[17],
            dethYn = list[18],
            rptType = list[19],
            rmk = list[20],
            instNm = list[21],
            instId = list[22],
            instTelno = list[23].replace("-", ""),
            instAddr = list[24],
            diagDrNm = list[25],
            rptChfNm = list[26],
        )
    }

    private fun splitAddress(address: String): List<String?> {
        val addr = address.replace("\n()", "") // \n() 삭제
        val fullAddr = addr.replace(Regex("\\s*\\([^)]*\\)"), "") // (...) 부분 삭제
        log.debug("NaverApiHandler splitAddress >>>>> $fullAddr")
        val list = mutableListOf<String?>()
        val splitedAddr = fullAddr.split(" ")

        list.add(StringUtils.getDstrCd1(splitedAddr[0])) // dstr1Cd
        list.add(splitedAddr[1]) // dstr2Cd
        list.add(splitedAddr.subList(0, 4).joinToString(" ")) // baseAddr

        // dtlAddr 상세주소가 있는 경우, 없으면 null
        if (splitedAddr.size > 4) {
            list.add(splitedAddr[4])
        } else {
            list.add(null)
        }

        list.add(fullAddr) // fullAddr

        return list
    }
}