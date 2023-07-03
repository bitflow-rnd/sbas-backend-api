package org.sbas.services

import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestResponse
import org.sbas.dtos.PagingListDto
import org.sbas.dtos.info.*
import org.sbas.entities.info.InfoCrewId
import org.sbas.entities.info.InfoInst
import org.sbas.parameters.SearchHospRequest
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

    /**
     * 의료기관(병원) 목록 조회
     */
    @Transactional
    fun findInfoHospList(searchParam: SearchHospRequest?): CommonResponse<PagingListDto> {
        val findHosp = infoHospRepository.findInfoHopByCondition(searchParam)

        val count = findHosp.count()
        val resultList = if(searchParam?.pageRequest == null){
            findHosp.page(0, 10).list() as MutableList
        }else {
            findHosp.page(searchParam.pageRequest?.page!! -1, searchParam.pageRequest?.size!!).list() as MutableList
        }

        val result = PagingListDto(count, resultList)

        return CommonResponse(result)
    }

    /**
     * 의료기관 상세 조회
     */
    fun findInfoHospById(hpId: String) : CommonResponse<InfoHospDetailDto> {
        val findResult = infoHospRepository.findInfoHospDetail(hpId)

        return CommonResponse(findResult)
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
    fun regFireStatn(fireStatnSaveReq: FireStatnSaveReq): CommonResponse<String> {
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
    fun findFireStatns(param: FireStatnSearchParam): CommonResponse<*> {
        val fireStatnList = infoInstRepository.findFireStatns(param)
        val crewCountList = infoCrewRepository.countInfoCrewsGroupByInstId()

        fireStatnList.forEach { dto ->
            crewCountList.forEach {
                if (dto.instId == it.instId) {
                    dto.crewCount = it.crewCount
                }
            }
        }

        return CommonResponse(mutableMapOf("count" to fireStatnList.size, "items" to fireStatnList))
    }

    /**
     * 구급대 상세
     */
    @Transactional
    fun findFireStatn(instId: String): CommonResponse<FireStatnDto> {
        val findFireStatn = infoInstRepository.findFireStatn(instId) ?: throw NotFoundException("$instId firestatn not found")

        return CommonResponse(findFireStatn.toFireStatnDto())
    }

    /**
     * 구급대원 목록 조회
     */
    @Transactional
    fun findFiremen(param: InfoCrewSearchParam): CommonResponse<MutableMap<String, Any>> {
        log.debug("findFiremen >>>>>> instId: ${param.instId}")
        val infoCrewList = infoCrewRepository.findInfoCrews(param)
        return CommonResponse(mutableMapOf("count" to infoCrewList.size, "items" to infoCrewList))
    }

    /**
     * 구급대원 조회
     */
    @Transactional
    fun findFireman(instId: String, crewId: String): CommonResponse<InfoCrewDto> {
        val findInfoCrew = infoCrewRepository.findInfoCrew(instId, crewId)
            ?: throw CustomizedException("해당 구급대원이 없습니다.", RestResponse.Status.NOT_FOUND)

        return CommonResponse(findInfoCrew.toInfoCrewDto())
    }

    /**
     * 구급대원 등록
     */
    @Transactional
    fun regFireman(infoCrewRegDto: InfoCrewRegDto): CommonResponse<String> {
        infoCrewRepository.persist(infoCrewRegDto.toEntityForInsert())

        return CommonResponse("등록 성공")
    }

    /**
     * 구급대원 수정
     */
    @Transactional
    fun modFireman(infoCrewRegDto: InfoCrewRegDto): CommonResponse<String>{
        val findInfoCrew = infoCrewRepository.findById(InfoCrewId(infoCrewRegDto.instId, infoCrewRegDto.crewId))
            ?: throw CustomizedException("해당 구급대원이 없습니다.", RestResponse.Status.NOT_FOUND)

        findInfoCrew.update(infoCrewRegDto)

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