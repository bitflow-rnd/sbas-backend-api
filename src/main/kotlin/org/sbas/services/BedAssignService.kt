package org.sbas.services

import org.jboss.logging.Logger
import org.sbas.dtos.BdasEsvyDto
import org.sbas.repositories.BdasEsvyRepository
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
    private lateinit var infoPtRepository: InfoPtRepository

    @Transactional
    fun regDisesInfo(bdasEsvyDto: BdasEsvyDto): CommonResponse<String> {
        // 환자 정보 저장
        val findInfoPt = infoPtRepository.findById(bdasEsvyDto.ptId) ?: throw NotFoundException("${bdasEsvyDto.ptId} not found")
        bdasEsvyDto.saveInfoPt(findInfoPt)

        // histSeq 최댓값 찾기
        val bdasEsvy = bdasEsvyRepository.findLatestRecordByPtId(findInfoPt.id!!)
        if (bdasEsvy != null) { // 수정하는 경우
            // 찾은 엔티티 histCd "C"로 update
            bdasEsvy.setHistCdAsC()
            // 수정 정보 다시 insert
            val updateEntity = bdasEsvyDto.toUpdateEntity(bdasEsvy.bdasSeq!!, bdasEsvy.histSeq!!)
            bdasEsvyRepository.persist(updateEntity)
        } else { // 처음 등록일 경우
            val maxBdasSeq = bdasEsvyRepository.findLatestBdasSeq()
            bdasEsvyRepository.persist(bdasEsvyDto.toEntity(maxBdasSeq + 1))
        }

        return CommonResponse("등록 성공")
    }
}