package org.sbas.handlers

import io.netty.util.internal.StringUtil
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.constants.NaverApiConst
import org.sbas.constants.enums.NatiCd
import org.sbas.repositories.BaseCodeRepository
import org.sbas.responses.patient.EpidResult
import org.sbas.restclients.NaverOcrRestClient
import org.sbas.restparameters.NaverOcrApiParams
import org.sbas.restparameters.OcrApiImagesParam
import org.sbas.restresponses.FieldName
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
    private lateinit var log: Logger

    @RestClient
    private lateinit var naverOcrClient: NaverOcrRestClient

    @ConfigProperty(name = "domain.this")
    private lateinit var serverdomain: String

    @ConfigProperty(name = "upload.path.middle")
    private lateinit var uploadRelPath: String

    @Inject
    private lateinit var baseCodeRepository: BaseCodeRepository

    @Inject
    private lateinit var geocodingHandler: GeocodingHandler

    fun recognizeImage(uri: String, filename: String, attcId: String?): EpidResult {
        val dotIdx = filename.lastIndexOf(".")

        val image = OcrApiImagesParam(
            filename.substring(dotIdx + 1),
            "epidreportimg",
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
        val fields = res.images[0].fields

        val nameToInferTextMap = fields?.associate { field ->
            field.name to if (StringUtil.isNullOrEmpty(field.inferText)) null else field.inferText
        }

        val nameList = FieldName.nameList
        val filteredMap = nameToInferTextMap?.filterKeys { it in nameList }
        val nullHandledMap = FieldName.nameList.associateWith { filteredMap?.get(it) }

        val rrno = nullHandledMap["주민등록번호"]
        val address = nullHandledMap["주소"]
        val splitAddress = splitAddress(address!!)

        return EpidResult(
            rcptPhc = nullHandledMap["수신보건소"],
            ptNm = nullHandledMap["환자이름"],
            rrno1 = getRrnoAndGndr(rrno)?.get("rrno1"),
            rrno2 = getRrnoAndGndr(rrno)?.get("rrno2"),
            gndr = getRrnoAndGndr(rrno)?.get("gndr"),
            nokNm = nullHandledMap["보호자명"],
            telno = nullHandledMap["전화번호"]?.replace("-", ""),

            dstr1Cd = splitAddress[0],
            dstr2Cd = splitAddress[1],
            baseAddr = splitAddress[2],
            dtlAddr = splitAddress[3],
            fullAddr = splitAddress[4],

            mpno = nullHandledMap["휴대전화번호"]?.replace("-", ""),
            diagNm = nullHandledMap["질병명"],
            diagGrde = nullHandledMap["질병급"],
            job = nullHandledMap["직업"],
            cv19Symp = nullHandledMap[nameList[11]],
            occrDt = nullHandledMap[nameList[12]],
            diagDt = nullHandledMap[nameList[13]],
            rptDt = nullHandledMap[nameList[14]],
            dfdgExamRslt = nullHandledMap[nameList[15]],
            ptCatg = nullHandledMap[nameList[16]],
            admsYn = nullHandledMap[nameList[17]],
            dethYn = nullHandledMap[nameList[18]],
            rptType = nullHandledMap[nameList[19]],
            rmk = nullHandledMap[nameList[20]],
            instNm = nullHandledMap[nameList[21]],
            instId = nullHandledMap[nameList[22]],
            instTelno = nullHandledMap[nameList[23]]?.replace("-", ""),
            instAddr = nullHandledMap[nameList[24]],
            diagDrNm = nullHandledMap[nameList[25]],
            rptChfNm = nullHandledMap[nameList[26]],
            natiCd = getNatiCd( rrno2 = rrno?.split("-")?.get(1) ),
            attcId = attcId,
        )
    }

    private fun splitAddress(address: String): List<String?> {
        val addr = address.replace("\n()", "").replace("\n", "")
            .replace(Regex("\\s*\\([^)]*\\)"), "") // (...) 부분 삭제
        val fullAddr = removeLeadingSpace(addr)
        log.debug("NaverApiHandler splitAddress >>>>> $fullAddr")

        val list = mutableListOf<String?>()
        val splitAddr = fullAddr.split(" ").toMutableList()

        val dstrCd1 = StringUtils.getDstrCd1(splitAddr[0])
        splitAddr[0] = StringUtils.getKakaoSidoName(splitAddr[0])
        val baseCode = baseCodeRepository.findByDstr1CdAndCdNm(dstrCd1, splitAddr[1]) ?: throw NotFoundException("baseCode not found")

        list.add(dstrCd1) // dstr1Cd
        list.add(baseCode.id.cdId) // dstr2Cd
        list.add(splitAddr.subList(0, 4).joinToString(" ")) // baseAddr

        // dtlAddr 상세주소가 있는 경우, 없으면 null
        if (splitAddr.size > 4) {
            list.add(splitAddr[4])
        } else {
            list.add(null)
        }

        list.add(fullAddr) // fullAddr

        return list
    }

//    private fun splitAddress(address: String): List<String?> {
//
//        val addr = address.replace("\n()", "") // \n() 삭제
//        val fullAddr = addr.replace(Regex("\\s*\\([^)]*\\)"), "") // (...) 부분
//
//        val siDoMap = StringUtils.siDoMap
//        val siDo = siDoMap.keys.filter { fullAddr.contains(it) }[0]
//        val dstrCd1 = StringUtils.getDstrCd1(siDo)
//        val siGunGu = address.substringAfter(siDo).split(" ")[1]
//
//        val list = mutableListOf<String?>()
////        val splitedAddr = fullAddr.split(" ").toMutableList()
////
//        val baseCode = baseCodeRepository.findByDstr1CdAndCdNm(dstrCd1, siGunGu) ?: throw NotFoundException("baseCode not found")
////
//        list.add(dstrCd1) // dstr1Cd
//        list.add(baseCode.id.cdId) // dstr2Cd
////        list.add(splitedAddr.subList(0, 4).joinToString(" ")) // baseAddr
////
////        // dtlAddr 상세주소가 있는 경우, 없으면 null
////        if (splitedAddr.size > 4) {
////            list.add(splitedAddr[4])
////        } else {
////            list.add(null)
////        }
////
////        list.add(fullAddr) // fullAddr
//
//        return list
//    }

    private fun getRrnoAndGndr(rrno: String?): MutableMap<String, String>? {
        if (rrno.isNullOrBlank()) {
            return null
        }
        val rrno1 = rrno.split("-")[0]
        val rrno2 = rrno.split("-")[1].first().toString()

        val gndr = when (rrno2) {
            "1", "3", "5", "7" -> "남"
            "2", "4", "6", "8" -> "여"
            else -> ""
        }

        return mutableMapOf("rrno1" to rrno1, "rrno2" to rrno2, "gndr" to gndr)
    }

    private fun getNatiCd(rrno2: String?): NatiCd? {
        if (rrno2.isNullOrBlank()) {
            return null
        }
        var rrno: String = rrno2
        if (rrno2.length > 1) {
            rrno = rrno2.first().toString()
        }

        return when {
            rrno.toInt() >= 5 -> NatiCd.NATI0002
            else -> NatiCd.NATI0001
        }
    }

    fun removeLeadingSpace(input: String): String {
        if (input.startsWith(" ")) {
            return input.substring(1)
        }
        return input
    }
}