package org.sbas.services

import org.jboss.logging.Logger
import org.sbas.dtos.BdasEsvyDto
import org.sbas.dtos.BdasReqSvrInfo
import org.sbas.entities.bdas.BdasReq
import org.sbas.entities.bdas.BdasReqId
import org.sbas.repositories.BdasEsvyRepository
import org.sbas.repositories.BdasReqRepository
import org.sbas.repositories.InfoPtRepository
import org.sbas.responses.CommonResponse
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

    @Transactional
    fun regDisesInfo(bdasEsvyDto: BdasEsvyDto): CommonResponse<*> {
        // 환자 정보 저장
        val findInfoPt = infoPtRepository.findById(bdasEsvyDto.ptId) ?: throw NotFoundException("${bdasEsvyDto.ptId} not found")
        bdasEsvyDto.saveInfoPt(findInfoPt)

        val res = mutableMapOf<String, Any>(
            Pair("ptId", findInfoPt.id!!),
        )

        val findBdasEsvy = bdasEsvyRepository.findByPtIdWithLatestBdasSeq(findInfoPt.id!!)
        if (findBdasEsvy != null) { // 수정하는 경우
            findBdasEsvy.bdasSeq
        } else { // 처음 등록일 경우
            log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            val bdasEsvy = bdasEsvyDto.toEntity()
            bdasEsvyRepository.persist(bdasEsvy)
            log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            res["bdasSeq"] = bdasEsvy.bdasSeq!!
        }

        return CommonResponse(res)
    }

    @Transactional
    fun regBioInfo(bdasReqSvrInfo: BdasReqSvrInfo): CommonResponse<*> {
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

        val res = mutableMapOf<String, Any>(
            Pair("ptId", bdasReq.id.ptId!!),
            Pair("bdasSeq", bdasReq.id.bdasSeq!!),
        )

        return CommonResponse(res)
    }

    @Transactional
    fun regServInfo(bdasReqSvrInfo: BdasReqSvrInfo): CommonResponse<String> {
        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(bdasReqSvrInfo.ptId, bdasReqSvrInfo.bdasSeq)
        if (findBdasReq != null) { // 중증도 분류 정보 등록 후 넘어오는 경우
            // 기존 bdasReq 엔티티에 SvrInfo 저장
            findBdasReq.saveSvrInfoFrom(bdasReqSvrInfo)
        } else { // 새로 저장
            val bdasReqId = BdasReqId(bdasReqSvrInfo.ptId, bdasReqSvrInfo.bdasSeq)
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
            
            // SvrInfo 저장
            bdasReq.saveSvrInfoFrom(bdasReqSvrInfo)
        }

        return CommonResponse("등록 성공")
    }
}