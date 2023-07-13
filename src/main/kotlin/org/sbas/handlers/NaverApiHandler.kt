package org.sbas.handlers

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.constants.NaverApiConst
import org.sbas.constants.enums.NatiCd
import org.sbas.repositories.BaseCodeRepository
import org.sbas.responses.patient.EpidResult
import org.sbas.restclients.NaverOcrRestClient
import org.sbas.restparameters.NaverGeocodingApiParams
import org.sbas.restparameters.NaverOcrApiParams
import org.sbas.restparameters.OcrApiImagesParam
import org.sbas.utils.StringUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.NotFoundException


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

    @Inject
    private lateinit var baseCodeRepository: BaseCodeRepository

    @Inject
    private lateinit var geocodingHandler: GeocodingHandler

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

        val splitAddress = splitAddress(address)

        return EpidResult(
            rcptPhc = list[0],
            ptNm = list[1],
            rrno1 = list[2].split("-")[0],
            rrno2 = list[2].split("-")[1],
            nokNm = list[3],
            gndr = list[4],
            telno = list[5].replace("-", ""),
            dstr1Cd = splitAddress[0],
            dstr2Cd = splitAddress[1],
            baseAddr = splitAddress[2],
            dtlAddr = splitAddress[3],
            fullAddr = splitAddress[4],
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
            zip = splitAddress[5],
            natiCd = getNatiCd(list[2].split("-")[1]),
        )
    }

    private fun splitAddress(address: String): List<String?> {
        val addr = address.replace("\n()", "") // \n() 삭제
        val fullAddr = addr.replace(Regex("\\s*\\([^)]*\\)"), "") // (...) 부분 삭제
        log.debug("NaverApiHandler splitAddress >>>>> $fullAddr")
        val list = mutableListOf<String?>()
        val splitedAddr = fullAddr.split(" ").toMutableList()

        val dstrCd1 = StringUtils.getDstrCd1(splitedAddr[0])
        splitedAddr[0] = StringUtils.getKakaoSidoName(splitedAddr[0])
        val baseCode = baseCodeRepository.findByDstr1CdAndCdNm(dstrCd1,splitedAddr[1]) ?: throw NotFoundException("baseCode not found")

        list.add(dstrCd1) // dstr1Cd
        list.add(baseCode.id.cdId) // dstr2Cd
        list.add(splitedAddr.subList(0, 4).joinToString(" ")) // baseAddr

        // dtlAddr 상세주소가 있는 경우, 없으면 null
        if (splitedAddr.size > 4) {
            list.add(splitedAddr[4])
        } else {
            list.add(null)
        }

        list.add(fullAddr) // fullAddr
        list.add(getZipCode(fullAddr)) // zip

        return list
    }

    private fun getZipCode(address: String): String {
        val response = geocodingHandler.getGeocoding(NaverGeocodingApiParams(query = address))

        val zip = response.addresses!![0].addressElements?.find {
            it.types?.get(0) == "POSTAL_CODE"
        }?.longName ?: ""

        return zip
    }

    private fun getNatiCd(rrno2: String?): NatiCd? {
        if (rrno2.isNullOrBlank()) {
            return null
        }

        return when {
            rrno2.toInt() >= 5 -> NatiCd.NATI0002
            else -> NatiCd.NATI0001
        }
    }
}