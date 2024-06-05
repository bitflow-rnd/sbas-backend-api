package org.sbas.handlers

import io.netty.util.internal.StringUtil
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
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
import org.sbas.restresponses.FieldName
import org.sbas.utils.StringUtils


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
            "$serverdomain$uri/$filename"
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
        val splitAddress = splitAddressUsingNaver(address!!)

        return EpidResult(
            rcptPhc = nullHandledMap["수신보건소"],
            ptNm = nullHandledMap["환자이름"],
            rrno1 = getRrnoAndGndr(rrno)?.get("rrno1"),
            rrno2 = getRrnoAndGndr(rrno)?.get("rrno2"),
            gndr = getRrnoAndGndr(rrno)?.get("gndr"),
            nokNm = nullHandledMap["보호자명"],
            telno = nullHandledMap["전화번호"]?.replace("-", ""),

            dstr1Cd = splitAddress.dstr1Cd,
            dstr2Cd = splitAddress.dstr2Cd,
            baseAddr = splitAddress.baseAddr,
            dtlAddr = splitAddress.dtlAddr,
            fullAddr = splitAddress.fullAddr,
            zip = splitAddress.zip,

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
            natiCd = getNatiCd(rrno2 = rrno?.split("-")?.get(1)),
            attcId = attcId,
        )
    }

    private fun splitAddress(address: String): AddressMap {
        // 역학조사서 주소 가공
        val fullAddr = address.replace("\n()", "")
            .replace("\n", "")
            .replace(Regex("\\([^)]*\\)"), "") // 괄호 부분 삭제
            .replace(",", "")
            .trimIndent()
        log.debug("NaverApiHandler splitAddress >>>>> $fullAddr")

        // 기본 주소, 세부 주소로 나누기
        val addrList = fullAddr.split(" ").toMutableList()
        addrList[0] = StringUtils.getKakaoSidoName(addrList[0])

        val roadNameIdx = addrList.indexOfFirst { it.endsWith("길") || it.endsWith("로") }
        val baseAddr = addrList.subList(0, roadNameIdx + 2).joinToString(" ").trimIndent()
        val dtlAddr = addrList.subList(roadNameIdx + 2, addrList.size).joinToString(" ").trimIndent()

        // 기본 주소로 네이버 주소 검색 api 이용
        val geocoding = geocodingHandler.getGeocoding(NaverGeocodingApiParams(query = baseAddr))

        // 우편번호
        val zip = if (geocoding.addresses.isNullOrEmpty()) {
            null
        } else {
            val addressElements = geocoding.addresses!![0].addressElements
            addressElements?.first { it.types!![0] == "POSTAL_CODE" }?.longName
        }

        // 코드
        val dstr1Cd = StringUtils.getDstr1Cd(addrList[0])
        val baseCode = baseCodeRepository.findByDstr1CdAndCdNm(dstr1Cd, addrList[1])
        val dstr2Cd = baseCode.id.cdId

        return AddressMap(
            dstr1Cd = dstr1Cd,
            dstr2Cd = dstr2Cd,
            baseAddr = baseAddr,
            dtlAddr = dtlAddr.ifBlank { null },
            fullAddr = fullAddr,
            zip = zip,
        )
    }

    private fun splitAddressUsingNaver(address: String): AddressMap {
        val geocodingAddress = geocodingHandler.getGeocoding(NaverGeocodingApiParams(query = address)).addresses
        val resultMap: Map<String, String>
        if (geocodingAddress.isNullOrEmpty()) {
            return AddressMap(
                dstr1Cd = null,
                dstr2Cd = null,
                baseAddr = null,
                dtlAddr = null,
                fullAddr = null,
                zip = null,
            )
        } else {
            resultMap = geocodingAddress[0].addressElements!!.associate {
                it.types!![0] to it.longName!!
            }
        }
        val dstr1Cd = StringUtils.getDstr1Cd(resultMap["SIDO"]!!)
        val baseCode = baseCodeRepository.findByDstr1CdAndCdNm(dstr1Cd, resultMap["SIGUGUN"]!!)
        val dstr2Cd = baseCode.id.cdId

        return AddressMap(
            dstr1Cd = dstr1Cd,
            dstr2Cd = dstr2Cd,
            baseAddr = geocodingAddress[0].roadAddress,
            dtlAddr = null,
            fullAddr = null,
            zip = resultMap["POSTAL_CODE"],
        )
    }

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
}

data class AddressMap(
    val dstr1Cd: String?,
    val dstr2Cd: String?,
    val baseAddr: String?,
    val dtlAddr: String?,
    val fullAddr: String?,
    val zip: String?,
)