package org.sbas.services

import org.jboss.logging.Logger
import org.sbas.dtos.BdasEsvyDto
import org.sbas.dtos.BdasReqDprtInfo
import org.sbas.dtos.BdasReqSvrInfo
import org.sbas.entities.bdas.BdasReq
import org.sbas.entities.bdas.BdasReqId
import org.sbas.handlers.GeocodingHandler
import org.sbas.repositories.BdasEsvyRepository
import org.sbas.repositories.BdasReqRepository
import org.sbas.repositories.InfoPtRepository
import org.sbas.responses.CommonResponse
import org.sbas.restparameters.NaverGeocodingApiParams
import java.time.Instant
import java.time.ZoneId
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.NotFoundException

/**
 * 병상배정 관련 서비스 클래스
 */
@ApplicationScoped
class BedAssignService {

    @Inject
    private lateinit var log: Logger

    @Inject
    private lateinit var bdasEsvyRepository: BdasEsvyRepository

    @Inject
    private lateinit var bdasReqRepository: BdasReqRepository

    @Inject
    private lateinit var infoPtRepository: InfoPtRepository

    @Inject
    private lateinit var geoHandler: GeocodingHandler

    @Transactional
    fun regDisesInfo(bdasEsvyDto: BdasEsvyDto): CommonResponse<String> {
        // 환자 정보 저장
        val findInfoPt = infoPtRepository.findById(bdasEsvyDto.ptId) ?: throw NotFoundException("${bdasEsvyDto.ptId} not found")
        bdasEsvyDto.saveInfoPt(findInfoPt)
        
//        val findBdasEsvy = bdasEsvyRepository.findByPtIdWithLatestBdasSeq(findInfoPt.id!!)
//        if (findBdasEsvy != null) { // 수정하는 경우
//            findBdasEsvy.bdasSeq
//        } else { // 처음 등록일 경우
//            log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
//            val bdasEsvy = bdasEsvyDto.toEntity()
//            bdasEsvyRepository.persist(bdasEsvy)
//            log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
//            res["bdasSeq"] = bdasEsvy.bdasSeq!!
//        }

        val bdasEsvy = bdasEsvyDto.toEntity()
        bdasEsvyRepository.persist(bdasEsvy)
        
        return CommonResponse("등록 성공")
    }

    @Transactional
    fun regBioInfo(bdasReqSvrInfo: BdasReqSvrInfo): CommonResponse<String> {
        // bdasEsvy 에서 bdasSeq 가져오기
        val bdasEsvy = bdasEsvyRepository.findByPtIdWithLatestBdasSeq(bdasReqSvrInfo.ptId) ?: throw NotFoundException("${bdasReqSvrInfo.ptId} not found")

//        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(bdasEsvy.ptId, bdasEsvy.bdasSeq!!)
//        if (findBdasReq != null) { // 수정하는 경우
//        } else { // 새로 저장
//        }

        // 엔티티 새로 생성 후 persist
        val bdasReqId = BdasReqId(bdasEsvy.ptId, bdasEsvy.bdasSeq)
        val bdasReq = BdasReq(
            id = bdasReqId,
            reqDt = "",
            reqTm = "",
            ptTypeCd = "",
            reqBedTypeCd = "",
            dnrAgreYn = "",
            svrtIptTypeCd = "",
            svrtTypeCd = "",
            reqTypeCd = "",
            reqDstr1Cd = "",
            dprtDstrTypeCd = "",
            inhpAsgnYn = "",
        )
        bdasReqRepository.persist(bdasReq)
        
        // 중증도 분류 정보 저장
        bdasReq.saveBioInfoFrom(bdasReqSvrInfo)

        return CommonResponse("등록 성공")
    }

    @Transactional
    fun regServInfo(bdasReqSvrInfo: BdasReqSvrInfo): CommonResponse<String> {
        // bdasEsvy 에서 bdasSeq 가져오기
        val bdasEsvy = bdasEsvyRepository.findByPtIdWithLatestBdasSeq(bdasReqSvrInfo.ptId) ?: throw NotFoundException("${bdasReqSvrInfo.ptId} not found")

        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(bdasReqSvrInfo.ptId, bdasEsvy.bdasSeq!!)
        if (findBdasReq != null) { // 중증도 분류 정보 등록 후 넘어오는 경우
            // 기존 bdasReq 엔티티에 SvrInfo 저장
            findBdasReq.saveSvrInfoFrom(bdasReqSvrInfo)
        } else { // 새로 저장
            val bdasReqId = BdasReqId(bdasReqSvrInfo.ptId, bdasEsvy.bdasSeq!!)
            // 엔티티 새로 생성 후 persist
            val bdasReq = BdasReq(
                id = bdasReqId,
                reqDt = "",
                reqTm = "",
                ptTypeCd = "",
                reqBedTypeCd = "",
                dnrAgreYn = "",
                svrtIptTypeCd = "",
                svrtTypeCd = "",
                reqTypeCd = "",
                reqDstr1Cd = "",
                dprtDstrTypeCd = "",
                inhpAsgnYn = "",
            )
            bdasReqRepository.persist(bdasReq)
            
            // 중증 정보 저장
            bdasReq.saveSvrInfoFrom(bdasReqSvrInfo)
        }

        return CommonResponse("등록 성공")
    }

    @Transactional
    fun regstrtpoint(bdasReqDprtInfo: BdasReqDprtInfo): CommonResponse<String> {
        // bdasEsvy 에서 bdasSeq 가져오기
        val bdasEsvy = bdasEsvyRepository.findByPtIdWithLatestBdasSeq(bdasReqDprtInfo.ptId) ?: throw NotFoundException("${bdasReqDprtInfo.ptId} not found")

        // 저장되어 있는 bdasReq
        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(bdasReqDprtInfo.ptId, bdasEsvy.bdasSeq!!) ?: throw NotFoundException("bdasReq not found")

        // 출발지 위도, 경도 설정
        setDepartureCoordinates(bdasReqDprtInfo)

        // 요청 시간 설정
        updateBdasReqWithCurrentDateTime(bdasReqDprtInfo)
        
        // 출발지 정보 저장
        findBdasReq.saveDprtInfoFrom(bdasReqDprtInfo)

        return CommonResponse("등록 성공")
    }

    private fun setDepartureCoordinates(bdasReqDprtInfo: BdasReqDprtInfo) {
        val geocoding = geoHandler.getGeocoding(NaverGeocodingApiParams(query = bdasReqDprtInfo.dprtDstrBascAddr!!))
        bdasReqDprtInfo.dprtDstrLat = geocoding.addresses!![0].y // 위도
        bdasReqDprtInfo.dprtDstrLon = geocoding.addresses!![0].x // 경도
    }

    private fun updateBdasReqWithCurrentDateTime(bdasReqDprtInfo: BdasReqDprtInfo) {
        val atZone = Instant.now().atZone(ZoneId.systemDefault())
        val time = atZone.toLocalTime()
        bdasReqDprtInfo.reqDt = "${atZone.year}${atZone.month}${atZone.dayOfMonth}"
        bdasReqDprtInfo.reqTm = "${time.hour}${time.minute}${time.second}"
    }
}