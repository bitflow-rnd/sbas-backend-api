package org.sbas.services

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.JsonWebToken
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.constants.PtTypeCd
import org.sbas.constants.SbasConst
import org.sbas.constants.SvrtTypeCd
import org.sbas.constants.UndrDsesCd
import org.sbas.dtos.InfoPtDto
import org.sbas.dtos.toEntity
import org.sbas.entities.base.BaseAttc
import org.sbas.entities.info.InfoPt
import org.sbas.handlers.FileHandler
import org.sbas.handlers.NaverApiHandler
import org.sbas.parameters.NewsScoreParameters
import org.sbas.parameters.SearchParameters
import org.sbas.repositories.BaseAttcRepository
import org.sbas.repositories.InfoInstRepository
import org.sbas.repositories.InfoPtRepository
import org.sbas.repositories.InfoUserRepository
import org.sbas.responses.CommonResponse
import org.sbas.utils.DynamicQueryBuilder
import org.sbas.utils.StringUtils
import java.io.File
import java.io.IOException
import java.net.URL
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.NotFoundException


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
    private lateinit var fileHandler: FileHandler

    @Inject
    private lateinit var naverApiHandler: NaverApiHandler
    
    @Inject
    private lateinit var baseAttcRepository: BaseAttcRepository

    @Inject
    private lateinit var jwt: JsonWebToken

    @ConfigProperty(name = "domain.this")
    private lateinit var serverdomain: String

    @Inject
    private lateinit var dynamicQueryBuilder: DynamicQueryBuilder

    @Transactional
    fun saveInfoPt(infoPtDto: InfoPtDto): CommonResponse<String?> {
        val infoPt = infoPtDto.toEntity()
        infoPtRepository.persist(infoPt)
        return CommonResponse(infoPt.ptId)
    }

    @Transactional
    fun check(infoPtDto: InfoPtDto): CommonResponse<String> {
        val findInfoPt = infoPtRepository.findByPtNmAndRrno(
            ptNm = infoPtDto.ptNm,
            rrno1 = infoPtDto.rrno1,
            rrno2 = infoPtDto.rrno2,
        )

        if (findInfoPt != null) { // 등록된 환자 존재
            return CommonResponse("등록된 환자가 존재합니다.")
        }
        return CommonResponse("등록된 환자가 존재하지 않습니다.")
    }

    fun calculateNewsScore(param: NewsScoreParameters): CommonResponse<Int> {
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
        return CommonResponse(list.sum())
    }

    @Transactional
    fun uploadEpidReport(param: FileUpload): CommonResponse<*>? {
        val fileInfo = fileHandler.createPublicFile(param)
        if (fileInfo != null) {
            // Naver Clova OCR call
            val res = naverApiHandler.recognizeImage(fileInfo.uriPath, fileInfo.filename)
            log.debug("texts are $res")
            // Then move from public to private
            fileHandler.moveFilePublicToPrivate(fileInfo.localPath, fileInfo.filename)
            val item = BaseAttc(
                attcDt = StringUtils.getYyyyMmDd(),
                attcTm = StringUtils.getHhMmSs(),
                fileTypeCd = SbasConst.FileTypeCd.IMAGE,
                fileNm = fileInfo.filename,
                loclPath = fileInfo.localPath,
                uriPath = fileInfo.uriPath,
            )
            baseAttcRepository.persist(item)
            return CommonResponse(res)
        }
        return null
    }

    @Transactional
    fun findEpidReportByAttcId(attcId: String): CommonResponse<*> {
        val baseAttc = baseAttcRepository.find("attc_id = '$attcId'").firstResult() ?: throw NotFoundException("$attcId not found")

        val url = URL("$serverdomain${baseAttc.uriPath}/${baseAttc.fileNm}")

        return try {
            val inputStream = url.openStream()
            inputStream.close()

            val res = naverApiHandler.recognizeImage(baseAttc.uriPath!!, baseAttc.fileNm!!)
            CommonResponse(res)
        } catch (e: IOException) {
            throw NotFoundException("역학조사서 파일이 존재하지 않습니다.")
        }
    }

    @Transactional
    fun updateInfoPt(ptId: String, infoPtDto: InfoPtDto): CommonResponse<String> {
        val findInfoPt = infoPtRepository.findById(ptId) ?: throw NotFoundException("$ptId not found")
        findInfoPt.updateEntity(infoPtDto)
        return CommonResponse("$ptId 수정 성공")
    }

    @Transactional
    fun findInfoPtWithMyOrgan(): CommonResponse<*> {
        // infoUser 에 있는 dstrCd를 사용해도 되나?
        val infoUser = infoUserRepository.findById(jwt.name) ?: throw NotFoundException()
        val infoInst = infoInstRepository.findById(infoUser.instId!!) ?: throw NotFoundException()
        val infoPtList = infoPtRepository.findByDstrCd(infoInst.dstrCd1!!, infoInst.dstrCd2!!)

        val res = mutableMapOf<String, Any>()
        res["items"] = infoPtList
        res["count"] = infoPtList.count()

        return CommonResponse(res)
    }

    @Transactional
    fun findInfoPt(ptId: String): CommonResponse<InfoPt> {
        return CommonResponse(infoPtRepository.findById(ptId) ?: throw NotFoundException("$ptId not found"))
    }

    @Transactional
    fun findBdasHistInfo(ptId: String) {
        
    }

    @Transactional
    fun findInfoPtList(searchParam: SearchParameters): CommonResponse<*> {
        val query = infoPtRepository.findInfoPtList()
        query.forEach { dto ->
            if (dto.ptTypeCd != null) {
                val split = dto.ptTypeCd!!.split(";")
                dto.tagList!!.addAll(split.map { PtTypeCd.valueOf(it).cdNm })
            }
            if (dto.svrtTypeCd != null) {
                val split = dto.svrtTypeCd!!.split(";")
                dto.tagList!!.addAll(split.map { SvrtTypeCd.valueOf(it).cdNm })
            }
            if (dto.undrDsesCd != null) {
                val split = dto.undrDsesCd!!.split(";")
                dto.tagList!!.addAll(split.map { UndrDsesCd.valueOf(it).cdNm })
            }
        }

        val res = mutableMapOf<String, Any>()
        res["count"] = query.size
        res["items"] = query

        return CommonResponse(res)
    }

    @Transactional
    fun delEpidReport(attcId: String): CommonResponse<String> {
        val baseAttc = baseAttcRepository.find("attc_id = '$attcId'").firstResult() ?: throw NotFoundException("$attcId not found")

//        val uri = URI("$serverdomain${baseAttc.uriPath}/${baseAttc.fileNm}")
//        log.warn("uri>>>>>>>>>$uri")
//        val file = File(uri)
        val file = File("${baseAttc.uriPath}/${baseAttc.fileNm}")
//        val file = File("${baseAttc.loclPath}/${baseAttc.fileNm}")

        if (file.exists()) {
            log.warn("file path1 >>>>>>>>> ${file.path}")
        } else {
            log.warn("file path2 >>>>>>>>> ${file.path}")
        }

        val deleteById = baseAttcRepository.deleteByAttcId(attcId)

        if (deleteById == 1L) {
            return if (file.delete()) {
                CommonResponse("삭제 성공")
            } else {
                throw RuntimeException("")
            }
//            infoPtRepository.update("attc_id = null")
        } else {
            throw NotFoundException("$attcId delete fail")
        }
    }
}