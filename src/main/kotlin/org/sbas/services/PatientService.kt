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
import org.sbas.response.CommonResponse
import org.sbas.response.StringResponse
import org.sbas.response.patient.EpidResult
import org.sbas.utils.StringUtils
import java.math.BigDecimal
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
        val fileurinext = handler1.createPublicFile(param)
        if (fileurinext != null) {
            // Naver Clova OCR call
            val res = handler2.recognizeImage(fileurinext[0])
            log.debug("texts are $res")
            // Then move from public to private
            handler1.moveFilePublicToPrivate(fileurinext[0])
            val item = BaseAttc()
            item.uriPath = fileurinext[0]
            item.attcDt = StringUtils.getYyyyMmDd()
            item.attcTm = StringUtils.getHhMmSs()
            item.loclPath = fileurinext[1]
            item.uriPath = fileurinext[2]
            item.fileTypeCd = SbasConst.FileTypeCd.IMAGE
            item.rgstUserId = userId
            item.updtUserId = userId
            handler3.persist(item)
            return CommonResponse(SbasConst.ResCode.SUCCESS, null, res)
        }
        return null
    }

}