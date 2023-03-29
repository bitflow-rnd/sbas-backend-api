package org.sbas.services

import org.jboss.logging.Logger
import org.sbas.entities.info.InfoCrew
import org.sbas.entities.info.InfoHosp
import org.sbas.entities.info.InfoInst
import org.sbas.parameters.InstCdParameters
import org.sbas.repositories.InfoCrewRepository
import org.sbas.repositories.InfoHospRepository
import org.sbas.repositories.InfoInstRepository
import org.sbas.responses.CommonResponse
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.NotFoundException


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class OrganiztnService {

    @Inject
    private lateinit var log: Logger

    @Inject
    private lateinit var infoCrewRepository: InfoCrewRepository

    @Inject
    private lateinit var infoHospRepository: InfoHospRepository

    @Inject
    private lateinit var infoInstRepository: InfoInstRepository

    /**
     * 의료기관(병원) 목록 조회
     */
    @Transactional
    fun findInfoHospList(): List<InfoHosp> {
        return infoHospRepository.findAll().list()
    }

    /**
     * 의료기관 상세 조회
     */
    fun findInfoHospById(hospId: String): InfoHosp {
        return infoHospRepository.findById(hospId) ?: throw NotFoundException("$hospId Not found")
    }

    /**
     * 기관코드 목록 조회
     */
    @Transactional
    fun getInstCodes(dstrCd1: String?, dstrCd2: String?, instTypeCd: String?): CommonResponse<List<InfoInst>> {
        return CommonResponse(infoInstRepository.findInstCodeList(dstrCd1 ?: "", dstrCd2 ?: "", instTypeCd ?: ""))
    }

    @Transactional
    fun findFireStatns(param: InstCdParameters): List<InfoInst> {
        return infoInstRepository.findFireStatns(param)
    }

    @Transactional
    fun findInfoCrews(instId: String): List<InfoCrew> {
        return infoCrewRepository.findInfoCrews(instId)
    }
}