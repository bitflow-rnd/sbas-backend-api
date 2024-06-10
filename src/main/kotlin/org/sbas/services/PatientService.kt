package org.sbas.services

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.JsonWebToken
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.constants.SbasConst
import org.sbas.constants.enums.BedStatCd
import org.sbas.constants.enums.SvrtTypeCd
import org.sbas.dtos.info.*
import org.sbas.entities.info.InfoPt
import org.sbas.handlers.FileHandler
import org.sbas.handlers.NaverApiHandler
import org.sbas.parameters.NewsScoreParameters
import org.sbas.repositories.*
import org.sbas.responses.CommonListResponse
import org.sbas.responses.CommonResponse
import org.sbas.utils.CustomizedException
import org.sbas.utils.StringUtils
import java.io.File
import java.io.IOException
import java.net.URL


/**
 * 환자정보를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class PatientService {

    @Inject
    private lateinit var log: Logger

    @Inject
    private lateinit var infoPtRepository: InfoPtRepository

    @Inject
    private lateinit var infoUserRepository: InfoUserRepository

    @Inject
    private lateinit var infoInstRepository: InfoInstRepository

    @Inject
    private lateinit var baseCodeRepository: BaseCodeRepository

    @Inject
    private lateinit var bdasEsvyRepository: BdasEsvyRepository

    @Inject
    private lateinit var bdasReqRepository: BdasReqRepository

    @Inject
    private lateinit var fileHandler: FileHandler

    @Inject
    private lateinit var naverApiHandler: NaverApiHandler
    
    @Inject
    private lateinit var baseAttcRepository: BaseAttcRepository

    @Inject
    private lateinit var jwt: JsonWebToken

    @ConfigProperty(name = "domain.this")
    private lateinit var serverdomain: String

    /**
     * 환자 기본정보 등록
     */
    @Transactional
    fun saveInfoPt(infoPtDto: InfoPtDto): CommonResponse<String?> {
        //환자 주소(bascAddr)로 dstr1Cd, dstr2Cd 구하기
        val split = infoPtDto.bascAddr.split(" ")
        val dstr1Cd = StringUtils.getDstr1Cd(split[0])
        val findBaseCode = baseCodeRepository.findByDstr1CdAndCdNm(dstr1Cd, split[1])
        val infoPt = infoPtDto.toEntity(dstr1Cd, findBaseCode.id.cdId)

        infoPtRepository.persist(infoPt)

        return CommonResponse(infoPt.ptId)
    }

    /**
     * 환자 중복 유효성 검사
     */
    @Transactional
    fun checkInfoPt(dto: InfoPtCheckRequest): CommonResponse<*> {
        val findInfoPt = infoPtRepository.findByPtNmAndRrno(
            ptNm = dto.ptNm,
            rrno1 = dto.rrno1,
            rrno2 = dto.rrno2,
        )
        val baseCode = dto.dstr2Cd?.let { baseCodeRepository.findByCdId(it) }

        val infoPtResponse = findInfoPt?.let {
            InfoPtCheckResponse(
                ptId = it.ptId,
                ptNm = it.ptNm,
                gndr = it.gndr,
                rrno1 = it.rrno1,
                rrno2 = it.rrno2,
                dstr1Cd = it.dstr1Cd,
                dstr1CdNm = baseCode?.rmk,
                dstr2Cd = it.dstr2Cd,
                dstr2CdNm = baseCode?.cdNm,
                telno = it.telno,
                natiCd = it.natiCd,
                dethYn = it.dethYn,
                nokNm = it.nokNm,
                mpno = it.mpno,
                job = it.job,
                bascAddr = it.bascAddr,
                detlAddr = it.detlAddr,
                zip = it.zip,
                natiNm = it.natiNm
            )
        }

        if (findInfoPt != null) { // 등록된 환자 존재
            return CommonResponse(mutableMapOf("isExist" to true, "items" to  infoPtResponse))
        }

        return CommonResponse(mutableMapOf("isExist" to false, "items" to  null))
    }

    @Transactional
    fun updateInfoPt(ptId: String, infoPtDto: InfoPtDto): CommonResponse<String> {
        val findInfoPt = infoPtRepository.findById(ptId) ?: throw NotFoundException("$ptId not found")

        //환자 주소(bascAddr)로 dstr1Cd, dstr2Cd 구하기
        val split = infoPtDto.bascAddr.split(" ")
        val dstr1Cd = StringUtils.getDstr1Cd(split[0])
        val findBaseCode = baseCodeRepository.findByDstr1CdAndCdNm(dstr1Cd, split[1])
        infoPtDto.dstr1Cd = dstr1Cd
        infoPtDto.dstr2Cd = findBaseCode.id.cdId

        findInfoPt.updateEntity(infoPtDto)

        return CommonResponse("$ptId 수정 성공")
    }

    @Transactional
    fun findInfoPtWithMyOrgan(): CommonListResponse<InfoPt> {
        //TODO
        val infoUser = infoUserRepository.findById(jwt.name) ?: throw NotFoundException("infoUser not found")
        val infoInst = infoInstRepository.findById(infoUser.instId) ?: throw NotFoundException("infoInst not found")
        val infoPtList = infoPtRepository.findByDstrCd(infoInst.dstr1Cd, infoInst.dstr2Cd!!)

        return CommonListResponse(infoPtList)
    }

    @Transactional
    fun findBasicInfo(ptId: String): CommonResponse<*> {
        val infoPt = infoPtRepository.findById(ptId) ?: throw NotFoundException("$ptId not found")
        val bdasReq = bdasReqRepository.findByPtIdWithLatestBdasSeq(ptId)

        val bedStatCd: String = bdasReq?.bedStatCd ?: "BAST0001"

        val baseCode = infoPt.dstr2Cd?.let { baseCodeRepository.findByCdId(it) }
        val infoPtBasicInfo = InfoPtBasicInfo(
            ptId = infoPt.ptId,
            ptNm = infoPt.ptNm,
            gndr = infoPt.gndr,
            age = infoPtRepository.getAge(infoPt.rrno1, infoPt.rrno2),
            rrno1 = infoPt.rrno1,
            rrno2 = infoPt.rrno2,
            dstr1Cd = infoPt.dstr1Cd,
            dstr1CdNm = baseCode?.rmk,
            dstr2Cd = infoPt.dstr2Cd,
            dstr2CdNm = baseCode?.cdNm,
            bascAddr = infoPt.bascAddr,
            detlAddr = infoPt.detlAddr,
            zip = infoPt.zip,
            dethYn = infoPt.dethYn,
            natiCd = infoPt.natiCd,
            natiNm = infoPt.natiNm,
            mpno = infoPt.mpno,
            telno = infoPt.telno,
            nokNm = infoPt.nokNm,
            job = infoPt.job,
            attcId = infoPt.attcId,
            bedStatCd = bedStatCd,
            bedStatNm = bedStatCd.let { BedStatCd.valueOf(it).cdNm },
            undrDsesCd = infoPt.undrDsesCd,
            undrDsesEtc = infoPt.undrDsesEtc,
            monitoring = false,
        )

        return CommonResponse(infoPtBasicInfo)
    }

    @Transactional
    fun findBdasHistInfo(ptId: String): CommonListResponse<BdasHisInfo> {
        val bdasHisInfoList = infoPtRepository.findBdasHisInfo(ptId)
        bdasHisInfoList.forEachIndexed { idx, bdasHisInfo ->
            bdasHisInfo.order = "${bdasHisInfoList.size - idx}"
        }

        return CommonListResponse(bdasHisInfoList)
    }

    @Transactional
    fun findInfoPtList(param: InfoPtSearchParam): CommonResponse<*> {
        val list = infoPtRepository.findInfoPtList(param)
        val count = infoPtRepository.countInfoPtList(param)
        return CommonListResponse(list, count.toInt())
    }

    @Transactional
    fun findHospNmList(param: InfoPtSearchParam): CommonResponse<*> {
        val list = infoPtRepository.findHospNmList(param)
        return CommonListResponse(list)
    }

    fun calculateNewsScore(param: NewsScoreParameters): CommonResponse<Map<String, Any>> {
        val list = mutableListOf<Int>()
        when {
            param.breath < 9 -> list.add(0, 3)
            param.breath in 9 until 12 -> list.add(0, 1)
            param.breath in 12 until 21 -> list.add(0, 0)
            param.breath in 21 until 25 -> list.add(0, 2)
            else -> list.add(0, 3)
        }
        when {
            param.spo2 < 92.0 -> list.add(1, 3)
            param.spo2 >= 92.0 && param.spo2 < 94.0 -> list.add(1, 2)
            param.spo2 >= 94.0 && param.spo2 < 96.0 -> list.add(1, 1)
            else -> list.add(1, 0)
        }
        when (param.o2Apply) {
            "Y" -> list.add(2, 2)
            "N" -> list.add(2, 0)
            else -> list.add(2, 0)
        }
        when {
            param.sbp <= 90 -> list.add(3,3)
            param.sbp in 91..100 -> list.add(3,2)
            param.sbp in 101..110 -> list.add(3,1)
            param.sbp in 111..219 -> list.add(3,0)
            else -> list.add(3,3)
        }
        when {
            param.pulse <= 40 -> list.add(4, 3)
            param.pulse in 41..50 -> list.add(4, 1)
            param.pulse in 51..90 -> list.add(4, 0)
            param.pulse in 91..110 -> list.add(4, 1)
            param.pulse in 111..130 -> list.add(4, 2)
            else -> list.add(4, 3)
        }
        when (param.avpu) {
            "A" -> list.add(5, 0)
            "V","P","U" -> list.add(5, 3)
            else -> list.add(5, 0)
        }
        when {
            param.bdTemp < 35.1 -> list.add(6, 3)
            param.bdTemp >= 35.1 && param.bdTemp < 36.1 -> list.add(6, 1)
            param.bdTemp >= 36.1 && param.bdTemp < 38.1 -> list.add(6, 0)
            param.bdTemp >= 38.1 && param.bdTemp < 39.1 -> list.add(6, 1)
            param.bdTemp >= 39.1 -> list.add(6, 2)
        }

        val score = list.sum()

        val svrtTypeCd = when {
            score == 0 -> SvrtTypeCd.SVTP0001.name
            score in 1..4 -> SvrtTypeCd.SVTP0002.name
//            list.any { it == 3 } -> SvrtTypeCd.SVTP0003.name
            score in 5..6 -> SvrtTypeCd.SVTP0004.name
            score >= 7 -> SvrtTypeCd.SVTP0005.name
            else -> SvrtTypeCd.SVTP0007.name
        }
//        SVTP0001("무증상"),
//        SVTP0002("경증"),
//        SVTP0003("중등증"),
//        SVTP0004("준중증"),
//        SVTP0005("중증"),
//        SVTP0006("위중증"),
//        SVTP0007("미분류"),

        val svrtTypeCdNm = SvrtTypeCd.valueOf(svrtTypeCd).cdNm

        return CommonResponse(mapOf("score" to score, "svrtTypeCd" to svrtTypeCd, "svrtTypeCdNm" to svrtTypeCdNm))
    }

    @Transactional
    fun uploadEpidReport(param: FileUpload): CommonResponse<*>? {
        val fileDto = fileHandler.createPrivateFile(param)

        val attcGrpId = baseAttcRepository.getNextValAttcGrpId()
        val entity = fileDto.toPrivateEntity(attcGrpId = attcGrpId, fileTypeCd = SbasConst.FileTypeCd.IMAGE, "역학조사서")
        baseAttcRepository.persist(entity)

        // Naver Clova OCR call
        val res = naverApiHandler.recognizeImage(fileDto.uriPath, fileDto.fileName, entity.attcId)
        log.debug("texts are $res")

        return CommonResponse(res)
    }

    @Transactional
    fun readEpidReport(attcId: String): CommonResponse<*> {
        val baseAttc = baseAttcRepository.findByAttcId(attcId) ?: throw NotFoundException("$attcId not found")

        val url = URL("$serverdomain/${baseAttc.uriPath}/${baseAttc.fileNm}")

        return try {
            val inputStream = url.openStream()
            inputStream.close()

            val res = naverApiHandler.recognizeImage(baseAttc.uriPath, baseAttc.fileNm, baseAttc.attcId)
            CommonResponse(res)
        } catch (e: IOException) {
            throw NotFoundException("역학조사서 파일이 존재하지 않습니다.")
        }
    }

    @Transactional
    fun delEpidReport(attcId: String): CommonResponse<String> {
        val baseAttc = baseAttcRepository.findByAttcId(attcId) ?: throw NotFoundException("$attcId not found")

        val file = File("${baseAttc.loclPath}/${baseAttc.fileNm}")
        log.debug("file path >>>>>>>>> ${file.path}")

        if (file.exists()) {
            val deleteById = baseAttcRepository.deleteByAttcId(attcId)

            if (deleteById == 1L) {
                return if (file.delete()) {
                    infoPtRepository.updateAttcId(attcId)
                    CommonResponse("삭제 성공")
                } else {
                    throw CustomizedException("삭제 실패", Response.Status.INTERNAL_SERVER_ERROR)
                }
            } else {
                throw CustomizedException("$attcId 삭제 실패", Response.Status.INTERNAL_SERVER_ERROR)
            }

        } else {
            log.debug("file path2 >>>>>>>>> ${file.path}")
            throw NotFoundException("file not found")
        }
    }
}