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
    fun regDisesInfo(bdasEsvyDto: BdasEsvyDto): CommonResponse<String> {
        // 환자 정보 저장
        val findInfoPt = infoPtRepository.findById(bdasEsvyDto.ptId) ?: throw NotFoundException("${bdasEsvyDto.ptId} not found")
        bdasEsvyDto.saveInfoPt(findInfoPt)

        // histSeq 최댓값 찾기
        val bdasEsvy = bdasEsvyRepository.findByPtIdWithLatestHistSeq(findInfoPt.id!!)
        if (bdasEsvy != null) { // 수정하는 경우
            // 찾은 엔티티 histCd "C"로 update
            bdasEsvy.setHistCdAsC()
            // 수정 정보 다시 insert
            val updateEntity = bdasEsvyDto.toUpdateEntity(
                bdasSeq = bdasEsvy.bdasSeq,
                histSeq = bdasEsvy.histSeq!!,
                )
            bdasEsvyRepository.persist(updateEntity)
        } else { // 처음 등록일 경우
            val maxBdasSeq = bdasEsvyRepository.findLatestBdasSeq()
            bdasEsvyRepository.persist(bdasEsvyDto.toEntity(maxBdasSeq + 1))
        }
        // TODO histSeq 설정
        return CommonResponse("등록 성공")
    }

    @Transactional
    fun regServInfo(bdasReqSvrInfo: BdasReqSvrInfo): CommonResponse<String> {
//        // bdasEsvy 에서 bdasSeq 가져오기
//        val bdasEsvy = bdasEsvyRepository.findByPtIdWithLatestBdasSeq(bdasReqSvrInfo.ptId) ?: throw NotFoundException("${bdasReqSvrInfo.ptId} not found")
//
//        // histSeq 생성
//        var bdasReq = bdasReqRepository.isRecordExist(bdasEsvy.ptId, bdasEsvy.bdasSeq) ?: throw NotFoundException("")
//        if (bdasReq != null) {
//
//        } else {
//
//        }
//
////        val bdasReqId = BdasReqId(bdasEsvy.ptId, bdasEsvy.bdasSeq, histSeq)
//
//        // SvrInfo 저장
////        bdasReqRepository.persist(bdasReqSvrInfo.toSvrtInfoEntity(bdasReqId))


        return CommonResponse("등록 성공")
    }

    @Transactional
    fun regBioInfo(bdasReqSvrInfo: BdasReqSvrInfo): CommonResponse<String> {
        // bdasEsvy 에서 bdasSeq 가져오기
        val bdasEsvy = bdasEsvyRepository.findByPtIdWithLatestBdasSeq(bdasReqSvrInfo.ptId) ?: throw NotFoundException("${bdasReqSvrInfo.ptId} not found")

        // histSeq 생성
        val bdasReqId: BdasReqId
        val findBdasReq = bdasReqRepository.isRecordExist(bdasEsvy.ptId, bdasEsvy.bdasSeq)
        if (findBdasReq != null) { // 수정하는 경우
            // 찾은 엔티티 histCd "C"로 update
            findBdasReq.setHistCdAsC()
            // histSeq + 1
            bdasReqId = BdasReqId(bdasEsvy.ptId, bdasEsvy.bdasSeq, findBdasReq.id.histSeq!!.plus(1))
        } else { // 새로 저장
            bdasReqId = BdasReqId(bdasEsvy.ptId, bdasEsvy.bdasSeq, 1)
        }
        
        // 엔티티 새로 생성 후 persist
        val bdasReq = BdasReq(
            id = bdasReqId,
            histCd = "Y",
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
}