package org.sbas.services

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.cache.CacheKey
import io.quarkus.cache.CacheResult
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.core.Response
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.json.JSONObject
import org.sbas.constants.SbasConst
import org.sbas.dtos.info.*
import org.sbas.entities.info.InfoCrewId
import org.sbas.entities.info.InfoHospDetail
import org.sbas.handlers.FileHandler
import org.sbas.handlers.GeocodingHandler
import org.sbas.repositories.*
import org.sbas.responses.CommonListResponse
import org.sbas.responses.CommonResponse
import org.sbas.restdtos.*
import org.sbas.utils.CustomizedException
import org.sbas.utils.StringUtils
import java.io.File

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
    private lateinit var infoUserRepository: InfoUserRepository

    @Inject
    private lateinit var baseCodeRepository: BaseCodeRepository

    @Inject
    private lateinit var baseAttcRepository: BaseAttcRepository

    @Inject
    private lateinit var infoHospDetailRepository: InfoHospDetailRepository

    @Inject
    private lateinit var infoBedRepository: InfoBedRepository

    @Inject
    private lateinit var geoHandler: GeocodingHandler

    @Inject
    private lateinit var fileHandler: FileHandler

    @Inject
    private lateinit var egenService: EgenService

    @Inject
    private lateinit var objectMapper: ObjectMapper

    /**
     * 의료기관(병원) 목록 조회
     */
    @Transactional
    fun findInfoHospList(param: InfoHospSearchParam): CommonResponse<*> {
        val findHosp = infoHospRepository.findInfoHosps(param)
        val count = infoHospRepository.countInfoHosps(param)

        log.debug(count)
        return CommonListResponse(findHosp, count)
    }

    /**
     * 의료기관 상세 조회
     */
    @CacheResult(cacheName = "infoHospDetail")
    fun findInfoHospById(@CacheKey hpId: String) : CommonResponse<HospInfoRes> {
        val jsonObject = egenService.getHsptlBassInfoInqire(param = EgenApiBassInfoParams(hpId = hpId))

        log.warn(jsonObject)

        val item = jsonObject.getJSONObject("item")

        val hospBasicInfo = objectMapper.readValue(item.toString(), HospBasicInfo::class.java)

        val (jsonObject2, totalCount) = egenService.getHsptlMdcncListInfoInqire(param = EgenApiListInfoParams(qn = hospBasicInfo.dutyName?.replace("\\s".toRegex(), "")))
        val bassInfo = if (totalCount == 1) {
            jsonObject2.getJSONObject("item")
        } else {
            jsonObject2.getJSONArray("item").first {
                it as JSONObject
                it.getString("hpid") == hospBasicInfo.hpid
            } as JSONObject
        }

        hospBasicInfo.dutyDiv = bassInfo.getString("dutyDiv")
        hospBasicInfo.dutyDivNam = bassInfo.getString("dutyDivNam")
        hospBasicInfo.dutyEmcls = bassInfo.getString("dutyEmcls")
        hospBasicInfo.dutyEmclsName = bassInfo.getString("dutyEmclsName")

        val (hospDetailInfo, attcId) = findHospDetailInfo(hpId)
        hospBasicInfo.attcId = attcId

        val hospMedInfo = findHospMedInfo(hpId)

        return CommonResponse(HospInfoRes(hospBasicInfo, hospDetailInfo, hospMedInfo, hospMedInfo.size))
    }

    /**
     * 기관코드 목록 조회
     */
    @Transactional
    fun getInstCodes(dstr1Cd: String?, dstr2Cd: String?, instTypeCd: String?): CommonListResponse<*> {
        val instList = when (instTypeCd) {
            "ORGN0004" -> infoHospRepository.findMediOrgan(dstr1Cd, dstr2Cd)
            else -> infoInstRepository.findInfoInst(dstr1Cd, dstr2Cd, instTypeCd)
        }

        return CommonListResponse(instList)
    }

    /**
     * 구급대 등록
     */
    @Transactional
    fun regFireStatn(fireStatnSaveReq: FireStatnSaveReq): CommonResponse<String> {
        val fireStatnInstId = StringUtils.incrementCode("FS", 8, infoInstRepository.findLatestFireStatInstId())

        val baseCode = baseCodeRepository.findByCdId(fireStatnSaveReq.dstr1Cd)
        val dstr2CdNm = baseCodeRepository.getDstr2CdNm(fireStatnSaveReq.dstr1Cd, fireStatnSaveReq.dstr2Cd)

        val fullAddr = baseCode!!.cdNm + dstr2CdNm + fireStatnSaveReq.detlAddr

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
        val count = infoInstRepository.countFireStatns(param)

        return CommonListResponse(fireStatnList, count)
    }

    /**
     * 구급대 상세
     */
    @Transactional
    fun findFireStatn(instId: String): CommonResponse<FireStatnDto> {
        val findFireStatn = infoInstRepository.findFireStatnDtoByInstId(instId)
            ?: throw NotFoundException("$instId firestatn not found")

        return CommonResponse(findFireStatn)
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
    fun findFireman(instId: String, crewId: Int): CommonResponse<InfoCrewDto> {
        val findInfoCrew = infoCrewRepository.findInfoCrew(instId, crewId)
            ?: throw CustomizedException("해당 구급대원이 없습니다.", RestResponse.Status.NOT_FOUND)

        return CommonResponse(findInfoCrew.toInfoCrewDto())
    }

    /**
     * 구급대원 등록
     */
    @Transactional
    fun regFireman(infoCrewSaveReq: InfoCrewSaveReq): CommonResponse<String> {
        val latestCrewId = infoCrewRepository.findLatestCrewId(infoCrewSaveReq.instId)
        infoCrewRepository.persist(infoCrewSaveReq.toEntityForInsert(latestCrewId + 1))

        return CommonResponse("등록 성공")
    }

    /**
     * 구급대원 수정
     */
    @Transactional
    fun modFireman(updateReq: InfoCrewUpdateReq): CommonResponse<String>{
        val findInfoCrew = infoCrewRepository.findById(InfoCrewId(updateReq.instId, updateReq.crewId))
            ?: throw CustomizedException("해당 구급대원이 없습니다.", RestResponse.Status.NOT_FOUND)

        findInfoCrew.update(updateReq)

        return CommonResponse("${findInfoCrew.crewNm} 구급대원의 정보가 수정되었습니다.")
    }

    /**
     * 구급대원 삭제
     */
    @Transactional
    fun delFireman(infoCrewDelReq: InfoCrewDelReq): CommonResponse<String>{
        val findInfoCrew = infoCrewRepository.findById(InfoCrewId(infoCrewDelReq.instId, infoCrewDelReq.crewId))
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
        val newAttcId = baseAttcRepository.getNextValAttcId()
        val entity = fileDto.toPrivateEntity(attcGrpId = attcGrpId, attcId = newAttcId, fileTypeCd = SbasConst.FileTypeCd.IMAGE, "${infoHosp.hospId} 이미지")
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

    private fun findHospDetailInfo(hpId: String): Pair<HospDetailInfo, String?> {
        val infoHosp = infoHospRepository.findInfoHospByHpId(hpId)

        val stage1 = if (infoHosp.dstr1CdNm == "강원도") {
            "강원특별자치도"
        } else {
            infoHosp.dstr1CdNm!!
        }

        val jsonObject = egenService.getEmrrmRltmUsefulSckbdInfoInqire(
            param = EgenApiEmrrmRltmUsefulSckbdInfoParams(
                stage1 = stage1,
                stage2 = null,
                pageNo = "1",
                numOfRows = "100",
            )
        )

        val detailInfo = jsonObject.first.getJSONArray("item").first {
            it as JSONObject
            it.getString("hpid") == hpId
        } as JSONObject

        val hospDetailInfo = objectMapper.readValue(detailInfo.toString(), HospDetailInfo::class.java)

        return Pair(hospDetailInfo, infoHosp.attcId)
    }

    @Transactional
    fun findHospMedInfo(hpId: String): MutableList<HospMedInfo> {
        return infoUserRepository.findMedicalInfoUser(hpId)
    }

    @Transactional
    fun findMedInstInfo(hospId: String): CommonResponse<InfoHospDetailDto?> {
        val hospDetail = infoHospDetailRepository.findById(hospId)
        return CommonResponse(hospDetail?.toResponse())
    }

    @Transactional
    fun modMedInstInfo(request: InfoHospDetail): CommonResponse<String> {
        val infoBed = infoBedRepository.findByHospId(request.hospId)
            ?: throw NotFoundException("${request.hospId} not found")

        val hospDetail = infoHospDetailRepository.findById(infoBed.hospId)

        if (hospDetail == null) {
            infoHospDetailRepository.persist(request)
        } else {
            hospDetail.update(request)
        }

        return CommonResponse("성공")
    }
}