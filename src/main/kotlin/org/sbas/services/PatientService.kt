package org.sbas.services

import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.constants.SbasConst
import org.sbas.dtos.InfoPtReq
import org.sbas.dtos.NewsScoreParam
import org.sbas.dtos.toEntity
import org.sbas.entities.base.BaseAttc
import org.sbas.entities.info.InfoPt
import org.sbas.handlers.FileHandler
import org.sbas.handlers.NaverApiHandler
import org.sbas.repositories.BaseAttcRepository
import org.sbas.repositories.InfoPtRepository
import org.sbas.responses.CommonResponse
import org.sbas.responses.patient.EpidResult
import org.sbas.utils.StringUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional


/**
 * 환자정보를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class PatientService {

    @Inject
    lateinit var log: Logger

    @Inject
    private lateinit var infoPtRepository: InfoPtRepository

    @Inject
    private lateinit var handler1: FileHandler

    @Inject
    private lateinit var handler2: NaverApiHandler
    
    @Inject
    private lateinit var handler3: BaseAttcRepository

    @Transactional
    fun saveInfoPt(infoPtReq: InfoPtReq): CommonResponse<String?> {
        val infoPt = infoPtReq.toEntity()
        infoPt.rgstUserId = "ADMIN"
        infoPt.updtUserId = "ADMIN"
        infoPtRepository.persist(infoPt)
        return CommonResponse(infoPt.id)
    }

    @Transactional
    fun check(infoPtReq: InfoPtReq): InfoPt? {
        val findInfoPt = infoPtRepository.findByPtNmAndRrno(
            ptNm = infoPtReq.ptNm,
            rrno1 = infoPtReq.rrno1,
            rrno2 = infoPtReq.rrno2,
        )

        if (findInfoPt != null) { // 등록된 환자 존재
            return findInfoPt
        }
        return null
    }

    fun calculateNewsScore(param: NewsScoreParam): Int {
        val list = mutableListOf<Int>()
        when {
            param.breath <= 8 -> list.add(0, 3)
            param.breath in 9..11 -> list.add(0, 1)
            param.breath in 12..20 -> list.add(0, 0)
            param.breath in 21..24 -> list.add(0, 2)
            else -> list.add(0, 3)
        }
        when {
            param.spo2 <= 91 -> list.add(1, 3)
            param.spo2 in 92..93 -> list.add(1, 2)
            param.spo2 in 94..95 -> list.add(1, 1)
            else -> list.add(1, 0)
        }
        when (param.o2Apply) {
            "Oxygen" -> list.add(2, 2)
            "Air" -> list.add(2, 0)
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
            "Alert" -> list.add(5, 0)
            "CVPU" -> list.add(5, 3)
            else -> list.add(5, 0)
        }
        when {
            param.bdTemp <= 35.0 -> list.add(6, 3)
            param.bdTemp in 35.1..36.0 -> list.add(6, 1)
            param.bdTemp in 36.1..38.0 -> list.add(6, 0)
            param.bdTemp in 38.1..39.0 -> list.add(6, 1)
            param.bdTemp >= 39.1 -> list.add(6, 2)
        }
        return list.sum()
    }

    @Transactional
    fun uploadEpidReport(param: FileUpload): CommonResponse<EpidResult>? {
        val userId = "test"
        val fileinfo = handler1.createPublicFile(param)
        if (fileinfo != null) {
            // Naver Clova OCR call
            val res = handler2.recognizeImage(fileinfo.uriPath, fileinfo.filename)
            log.debug("texts are $res")
            // Then move from public to private
            handler1.moveFilePublicToPrivate(fileinfo.localPath, fileinfo.filename)
            val item = BaseAttc()
            item.attcDt = StringUtils.getYyyyMmDd()
            item.attcTm = StringUtils.getHhMmSs()
            item.loclPath = fileinfo.localPath
            item.uriPath = fileinfo.uriPath
            item.fileNm = fileinfo.filename
            item.fileTypeCd = SbasConst.FileTypeCd.IMAGE
            item.rgstUserId = userId
            item.updtUserId = userId
            handler3.persist(item)
            return CommonResponse(SbasConst.ResCode.SUCCESS, null, res)
        }
        return null
    }

}