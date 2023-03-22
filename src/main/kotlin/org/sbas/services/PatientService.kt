package org.sbas.services

import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.constants.SbasConst
import org.sbas.entities.base.BaseAttc
import org.sbas.entities.info.InfoPt
import org.sbas.handlers.FileHandler
import org.sbas.handlers.NaverApiHandler
import org.sbas.repositories.BaseAttcRepository
import org.sbas.repositories.InfoPtRepository
import org.sbas.responses.CommonResponse
import org.sbas.responses.StringResponse
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
    fun saveInfoPt(infoPt: InfoPt): StringResponse {
        infoPt.rgstUserId = "jiseong"
        infoPt.updtUserId = "jiseong"
        infoPtRepository.persist(infoPt)
        return StringResponse(infoPt.id)
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