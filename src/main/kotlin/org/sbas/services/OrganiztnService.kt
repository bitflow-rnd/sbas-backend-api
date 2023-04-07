package org.sbas.services

import org.eclipse.microprofile.jwt.JsonWebToken
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestResponse
import org.sbas.dtos.FireStatnSaveReq
import org.sbas.dtos.InfoCrewRegDto
import org.sbas.dtos.InfoInstUpdateReq
import org.sbas.entities.info.*
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
    fun saveFireStatn(fireStatnSaveReq: FireStatnSaveReq): CommonResponse<String> {
        infoInstRepository.persist(fireStatnSaveReq.toEntity())
        return CommonResponse("등록 성공")
    }

    /**
     * 구급대 수정
     */
    @Transactional
    fun updateFireStatn(updateReq: InfoInstUpdateReq): CommonResponse<String> {
        val infoInst = infoInstRepository.findById(updateReq.instId)
            ?: throw CustomizedException("${updateReq.instId} not found", Response.Status.NOT_FOUND)
        infoInst.update(updateReq)

        return CommonResponse("${updateReq.instId}의 정보가 수정되었습니다.")
    }

    /**
     * 구급대 삭제
     */
    @Transactional
    fun deleteFireStatn(instId: String): CommonResponse<String> {
        val fireStatn = infoInstRepository.findById(instId)
            ?: throw CustomizedException("$instId not found", Response.Status.NOT_FOUND)

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
     * 구급대원 리스트 조회
     */
    @Transactional
    fun getFiremen(instId: String): CommonResponse<List<InfoCrew>> {
        val findInfoCrew = infoCrewRepository.findInfoCrews(instId)

        if(findInfoCrew.isEmpty()) throw NotFoundException("해당 구급대에 구급대원이 없습니다.")

        return CommonResponse(findInfoCrew)
    }

    /**
     * 구급대원 등록
     */
    @Transactional
    fun regFireman(infoCrewRegDto: InfoCrewRegDto): CommonResponse<String> {
        infoCrewRepository.persist(infoCrewRegDto.toEntityForInsert(jwt.name))

        return CommonResponse("등록 성공")
    }

    /**
     * 구급대원 수정
     */
    @Transactional
    fun modFireman(infoCrewRegDto: InfoCrewRegDto): CommonResponse<String>{
        val findInfoCrew = infoCrewRepository.findById(InfoCrewId(infoCrewRegDto.instId, infoCrewRegDto.crewId))
            ?: throw CustomizedException("해당 구급대원이 없습니다.", RestResponse.Status.NOT_FOUND)

        findInfoCrew.pstn = infoCrewRegDto.pstn
        findInfoCrew.crewNm = infoCrewRegDto.crewNm
        findInfoCrew.telno = infoCrewRegDto.telno
        findInfoCrew.rmk = infoCrewRegDto.rmk
        findInfoCrew.updtUserId = jwt.name

        return CommonResponse("${findInfoCrew.crewNm} 구급대원의 정보가 수정되었습니다.")
    }

    /**
     * 구급대원 삭제
     */
    @Transactional
    fun delFireman(infoCrewId: InfoCrewId): CommonResponse<String>{
        val findInfoCrew = infoCrewRepository.findById(infoCrewId)
            ?: throw CustomizedException("해당 구급대원이 없습니다.", RestResponse.Status.NOT_FOUND)

        infoCrewRepository.delete(findInfoCrew)

        return CommonResponse("${findInfoCrew.crewNm} 구급대원을 삭제했습니다.")
    }

}