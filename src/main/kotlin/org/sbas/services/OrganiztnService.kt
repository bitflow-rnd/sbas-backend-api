package org.sbas.services

import org.eclipse.microprofile.jwt.JsonWebToken
import org.jboss.logging.Logger
import org.sbas.dtos.FireStatnSaveReq
import org.sbas.dtos.InfoCrewRegDto
import org.sbas.dtos.InfoInstUpdateDto
import org.sbas.entities.info.InfoCrew
import org.sbas.entities.info.InfoHosp
import org.sbas.entities.info.InfoInst
import org.sbas.parameters.InstCdParameters
import org.sbas.repositories.InfoCrewRepository
import org.sbas.repositories.InfoHospRepository
import org.sbas.repositories.InfoInstRepository
import org.sbas.responses.CommonResponse
import org.sbas.utils.CustomizedException
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.NotFoundException
import javax.ws.rs.core.Response


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

    @Inject
    private lateinit var jwt: JsonWebToken

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

    /**
     * 구급대 등록
     */
    @Transactional
    fun saveFireStatn(fireStatnSaveReq: FireStatnSaveReq) {
        infoInstRepository.persist(fireStatnSaveReq.toEntity())
    }

    /**
     * 구급대 수정
     */
    @Transactional
    fun updateFireStatn(fireStatnUpdateReq: InfoInstUpdateDto) {

        infoInstRepository.getEntityManager().merge(fireStatnUpdateReq)

    }

    /**
     * 구급대 삭제
     */
    @Transactional
    fun deleteFireStatn(instId: String): CommonResponse<String> {
        val fireStatn = infoInstRepository.findById(instId) ?: throw NotFoundException("$instId not found")
        if (infoInstRepository.isPersistent(fireStatn)) {
            infoInstRepository.deleteById(instId)
            return CommonResponse("삭제 성공")
        } else {
//            return CommonResponse("삭제 실패")
            throw CustomizedException("삭제 실패", Response.Status.CONFLICT)
        }
    }


    /**
     * 구급대 목록
     */
    @Transactional
    fun findFireStatns(param: InstCdParameters): List<InfoInst> {
        return infoInstRepository.findFireStatns(param)
    }

    @Transactional
    fun findInfoCrews(instId: String): List<InfoCrew> {
        return infoCrewRepository.findInfoCrews(instId)
    }

    /**
     * 구급대원 등록
     */
    @Transactional
    fun regFireman(infoCrewRegDto: InfoCrewRegDto): CommonResponse<String> {
        infoCrewRegDto.adminId = jwt.name

        infoCrewRepository.persist(infoCrewRegDto.toEntity())

        return CommonResponse("등록 성공")
    }

}