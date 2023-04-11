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
        val findInfoPt = infoPtRepository.findById(bdasEsvyDto.ptId) ?: throw NotFoundException("${bdasEsvyDto.ptId} not found")
        bdasEsvyDto.saveInfoPt(findInfoPt)

        bdasEsvyRepository.persist(bdasEsvyDto.toEntity())
        return CommonResponse("등록 성공 ${bdasEsvyDto.toEntity()}")
    }
}