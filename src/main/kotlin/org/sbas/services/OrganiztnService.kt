package org.sbas.services

import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.constants.SbasConst
import org.sbas.dtos.info.*
import org.sbas.entities.info.InfoCrewId
import org.sbas.entities.info.InfoInst
import org.sbas.handlers.FileHandler
import org.sbas.handlers.GeocodingHandler
import org.sbas.repositories.*
import org.sbas.responses.CommonResponse
import org.sbas.responses.CommonListResponse
import org.sbas.restparameters.NaverGeocodingApiParams
import org.sbas.utils.CustomizedException
import org.sbas.utils.StringUtils
import java.io.File
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
    private lateinit var baseCodeRepository: BaseCodeRepository

    @Inject
    private lateinit var baseAttcRepository: BaseAttcRepository

    @Inject
    private lateinit var geoHandler: GeocodingHandler

    @Inject
    private lateinit var fileHandler: FileHandler

    /**
     * 의료기관(병원) 목록 조회
     */
    @Transactional
    fun findInfoHospList(param: InfoHospSearchParam): CommonResponse<*> {
        val dutyDivNam = mutableListOf<String>()
        param.dutyDivNams?.let {
            dutyDivNam.addAll(it.split(";"))
        }
        if (dutyDivNam.isEmpty()) {
            param.dutyDivNam = null
        } else {
            param.dutyDivNam = dutyDivNam
        }

        val findHosp = infoHospRepository.findInfoHosps(param)
        val count = infoHospRepository.countInfoHosps(param)

        return CommonListResponse(findHosp, count)
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
        val fireStatnInstId = StringUtils.incrementCode("FS", 8, infoInstRepository.findLatestFireStatInstId())

        val baseCode = baseCodeRepository.findBaseCodeByCdId(fireStatnSaveReq.dstrCd1 ?: "")
        val dstrCd2Nm = baseCodeRepository.getDstrCd2Nm(fireStatnSaveReq.dstrCd1, fireStatnSaveReq.dstrCd2)

        val fullAddr = baseCode!!.cdNm + dstrCd2Nm + fireStatnSaveReq.detlAddr

        val geocoding = geoHandler.getGeocoding(NaverGeocodingApiParams(query = fullAddr))
        fireStatnSaveReq.lat = geocoding.addresses!![0].y // 위도
        fireStatnSaveReq.lon = geocoding.addresses!![0].x // 경도

        infoInstRepository.persist(fireStatnSaveReq.toEntity(fireStatnInstId))

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

        return CommonListResponse(fireStatnList)
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
    fun findFiremen(param: InfoCrewSearchParam): CommonListResponse<InfoCrewDto> {
        log.debug("findFiremen >>>>>> instId: ${param.instId}")
        val infoCrewList = infoCrewRepository.findInfoCrews(param)
        return CommonListResponse(infoCrewList)
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
        val crewId = StringUtils.incrementCode("CR", 8, infoCrewRepository.findLatestCrewId(infoCrewRegDto.instId))

        infoCrewRepository.persist(infoCrewRegDto.toEntityForInsert(crewId))

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

    @Transactional
    fun uploadHospImg(hospId: String, fileUpload: FileUpload): CommonResponse<String> {
        val infoHosp = infoHospRepository.findInfoHospByHospId(hospId) ?: throw NotFoundException("$hospId not found")
        val attcId = infoHosp.attcId

        if (!attcId.isNullOrEmpty()) {
            val baseAttc = baseAttcRepository.findByAttcId(attcId) ?: throw NotFoundException("$attcId not found")
            val file = File("${baseAttc.loclPath}/${baseAttc.fileNm}")
            log.debug("file path >>>>>>>>> ${file.path}")

            if (file.exists()) {
                baseAttcRepository.deleteByAttcId(attcId)
                    ?: throw CustomizedException("$attcId 삭제 실패", Response.Status.INTERNAL_SERVER_ERROR)
                if (file.delete()) {
                    log.debug("uploadHospImg >>> delete 성공")
                } else {
                    throw CustomizedException("삭제 실패", Response.Status.INTERNAL_SERVER_ERROR)
                }

            } else {
                throw NotFoundException("file not found")
            }
        }

        val fileDto = fileHandler.createPrivateFile(fileUpload)

        val attcGrpId = baseAttcRepository.getNextValAttcGrpId()
        val entity = fileDto.toPrivateEntity(attcGrpId = attcGrpId, fileTypeCd = SbasConst.FileTypeCd.IMAGE, "${infoHosp.hospId} 이미지")
        baseAttcRepository.persist(entity)

        infoHosp.updateAttcId(entity.attcId)

        return CommonResponse("이미지 업로드 성공")
    }

    @Transactional
    fun deleteHospImg(hospId: String): CommonResponse<String> {
        val infoHosp = infoHospRepository.findInfoHospByHospId(hospId) ?: throw NotFoundException("$hospId not found")
        val attcId = infoHosp.attcId

        if (!attcId.isNullOrEmpty()) {
            val baseAttc = baseAttcRepository.findByAttcId(attcId) ?: throw NotFoundException("$attcId not found")
            val file = File("${baseAttc.loclPath}/${baseAttc.fileNm}")
            log.debug("file path >>>>>>>>> ${file.path}")

            if (file.exists()) {
                baseAttcRepository.deleteByAttcId(attcId)
                    ?: throw CustomizedException("$attcId 삭제 실패", Response.Status.INTERNAL_SERVER_ERROR)

                if (file.delete()) {
                    infoHosp.updateAttcId(null)
                    log.debug("deleteHospImg >>> delete 성공")
                } else {
                    throw CustomizedException("삭제 실패", Response.Status.INTERNAL_SERVER_ERROR)
                }

            } else {
                throw NotFoundException("file not found")
            }
        }
        return CommonResponse("이미지 삭제 성공")
    }
}