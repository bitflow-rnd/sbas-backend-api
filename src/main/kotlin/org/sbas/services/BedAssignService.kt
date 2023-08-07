package org.sbas.services

import org.jboss.logging.Logger
import org.sbas.constants.enums.BedStatCd
import org.sbas.constants.enums.TimeLineStatCd
import org.sbas.dtos.bdas.*
import org.sbas.entities.bdas.BdasReqId
import org.sbas.handlers.GeocodingHandler
import org.sbas.repositories.*
import org.sbas.responses.CommonListResponse
import org.sbas.responses.CommonResponse
import org.sbas.responses.patient.DiseaseInfoResponse
import org.sbas.restclients.FirebaseService
import org.sbas.restparameters.NaverGeocodingApiParams
import org.sbas.utils.CustomizedException
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.NotFoundException
import javax.ws.rs.core.Response
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * 병상배정 관련 서비스 클래스
 */
@ApplicationScoped
class BedAssignService {
    @Inject private lateinit var log: Logger
    @Inject private lateinit var bdasEsvyRepository: BdasEsvyRepository
    @Inject private lateinit var bdasReqRepository: BdasReqRepository
    @Inject private lateinit var bdasReqAprvRepository: BdasReqAprvRepository
    @Inject private lateinit var bdasAprvRepository: BdasAprvRepository
    @Inject private lateinit var bdasTrnsRepository: BdasTrnsRepository
    @Inject private lateinit var bdasAdmsRepository: BdasAdmsRepository

    @Inject private lateinit var infoPtRepository: InfoPtRepository
    @Inject private lateinit var infoHospRepository: InfoHospRepository
    @Inject private lateinit var infoUserRepository: InfoUserRepository

    @Inject private lateinit var baseCodeRepository: BaseCodeRepository
    @Inject private lateinit var geoHandler: GeocodingHandler
    @Inject private lateinit var firebaseService: FirebaseService

    /**
     * 질병 정보 등록
     */
    @Transactional
    fun regDisesInfo(saveRequest: BdasEsvySaveRequest): CommonResponse<String> {
        // 환자 정보 저장
        val findInfoPt = infoPtRepository.findById(saveRequest.ptId) ?: throw NotFoundException("${saveRequest.ptId} not found")
        saveRequest.saveInfoPt(findInfoPt)

        log. debug("regDisesInfo >>>>> ${saveRequest.ptId}")
        val bdasEsvy = saveRequest.toEntity()
        bdasEsvyRepository.persist(bdasEsvy)
        
        return CommonResponse("감염병 정보 등록 성공")
    }

    /**
     * 병상 요청
     */
    @Transactional
    fun registerBedRequestInfo(saveRequest: BdasReqSaveRequest): CommonResponse<String> {
        if (!saveRequest.isPtIdEqual()) {
            throw CustomizedException("check ptId", Response.Status.BAD_REQUEST)
        }

        val ptId = saveRequest.svrInfo.ptId
        val bdasReqDprtInfo = saveRequest.dprtInfo

        // bdasEsvy 에서 bdasSeq 가져오기
        val bdasEsvy = bdasEsvyRepository.findByPtIdWithLatestBdasSeq(ptId) ?: throw NotFoundException("$ptId not found")
        val bdasReqId = BdasReqId(ptId, bdasEsvy.bdasSeq)

        // 출발지 위도, 경도 설정
        val geocoding = geoHandler.getGeocoding(NaverGeocodingApiParams(query = bdasReqDprtInfo.dprtDstrBascAddr!!))
        bdasReqDprtInfo.dprtDstrLat = geocoding.addresses!![0].y // 위도
        bdasReqDprtInfo.dprtDstrLon = geocoding.addresses!![0].x // 경도

        val bdasReq = saveRequest.toEntity(bdasReqId)
        bdasReqRepository.persist(bdasReq)

        // 지역코드로 병상배정반 찾기
        val bdasUsers =
            infoUserRepository.findBdasUserByReqDstrCd(bdasReq.reqDstr1Cd, bdasReq.reqDstr2Cd)

        // 푸쉬 알람 보내기
        bdasUsers.forEach {
            log.debug("registerBedRequestInfo bdasUsers >>> ${it.id}")
//            firebaseService.sendMessage(bdasReq.updtUserId!!, "${bdasReqDprtInfo.msg}", it.id)
        }
        firebaseService.sendMessage(bdasReq.updtUserId!!, "${bdasReqDprtInfo.msg}", "TEST-APR-1")

        return CommonResponse("병상 요청 성공")
    }

    /**
     * 배정반 승인
     */
    @Transactional
    fun reqConfirm(saveRequest: BdasReqAprvSaveRequest): CommonResponse<String> {
        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(saveRequest.ptId, saveRequest.bdasSeq) ?: throw NotFoundException("bdasReq not found")

        // 배정반 승인/거절
        if (saveRequest.aprvYn == "N") { // 거절할 경우 거절 사유 및 메시지 작성
            bdasReqAprvRepository.persist(saveRequest.toRefuseEntity())
            findBdasReq.changeBedStatTo(BedStatCd.BAST0008.name)
        } else if (saveRequest.aprvYn == "Y") { // 승인할 경우 원내 배정 여부 체크
            if (findBdasReq.inhpAsgnYn == "N") {
                // 전원 요청시 병원 정보 저장
                val hospList = infoHospRepository.findByHospIdList(saveRequest.reqHospIdList)

                hospList.forEachIndexed { idx, infoHosp ->
                    log.debug("hospList>>>>>>>>>>> ${infoHosp.hospId}")
                    val entity = saveRequest.toEntityWhenNotInHosp(
                        asgnReqSeq = idx + 1,
                        hospId = infoHosp.hospId,
                        hospNm = infoHosp.dutyName,
                    )
                    bdasReqAprvRepository.persist(entity)
                    findBdasReq.changeBedStatTo(BedStatCd.BAST0004.name)

//                    firebaseService.sendMessage("jiseongtak", "${dto.msg}", infoHosp.userId)
                    firebaseService.sendMessage(entity.rgstUserId!!, "${saveRequest.msg}", "TEST-APR-1")
                }

            } else if (findBdasReq.inhpAsgnYn == "Y") {
                // 원내 배정이면 승인, 의료진 승인 건너뜀
                bdasReqAprvRepository.persist(saveRequest.toEntityWhenInHosp())
                findBdasReq.changeBedStatTo(BedStatCd.BAST0006.name)
            }
        } else {
            throw CustomizedException("aprvYn 값이 올바르지 않습니다.", Response.Status.INTERNAL_SERVER_ERROR)
        }

        return CommonResponse("승인 성공")
    }

    /**
     * 가용 병원 목록 조회
     */
    @Transactional
    fun getAvalHospList(ptId: String, bdasSeq: Int): CommonResponse<*> {
        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(ptId, bdasSeq) ?: throw NotFoundException("bdasReq not found")

        val dstrCd1 = findBdasReq.reqDstr1Cd
        val dstrCd2 = findBdasReq.reqDstr2Cd

        // dstrCd1, dstrCd2에 해당하는 infoHosp 목록
        val infoHospList = infoHospRepository.findListByDstrCd1AndDstrCd2(dstrCd1, dstrCd2)
        log.debug("getAvalHospList >>>>>>>>>>>>>> ${infoHospList.size}")
        val list = infoHospList.map {
            val distance = calculateDistance(
                lat1 = findBdasReq.dprtDstrLat!!.toDouble(),
                lon1 = findBdasReq.dprtDstrLon!!.toDouble(),
                lat2 = it.wgs84Lat!!.toDouble(),
                lon2 = it.wgs84Lon!!.toDouble(),
            )
            AvalHospListResponse(
                hospId = it.hospId!!,
                hospNm = it.dutyName!!,
                doubleDistance = distance,
                distance = convertToStringDistance(distance),
                addr = it.dutyAddr!!,
            )
        }

        // TODO 페이징 처리??
        if (list.size > 10) {
            val sortedList = list.subList(0, 10).sortedBy { it.doubleDistance }.distinctBy { it.hospId }
            return CommonListResponse(sortedList)
        }

        val sortedList = list.sortedBy { it.doubleDistance }.distinctBy { it.hospId }
        return CommonListResponse(sortedList)
    }

    /**
     * 의료진 승인
     */
    @Transactional
    fun asgnConfirm(saveRequest: BdasAprvSaveRequest): CommonResponse<BdasAprvResponse> {
        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(saveRequest.ptId, saveRequest.bdasSeq) ?: throw NotFoundException("bdasReq not found")

        if (findBdasReq.inhpAsgnYn == "Y") {
            throw CustomizedException("원내배정 입니다.", Response.Status.BAD_REQUEST)
        }

        val bdasReqAprvList = bdasReqAprvRepository.findReqAprvList(saveRequest.ptId, saveRequest.bdasSeq)
        if (bdasReqAprvList.isEmpty()) {
            throw CustomizedException("배정 승인 정보가 없습니다.", Response.Status.BAD_REQUEST)
        }

        val bdasAprvList = bdasAprvRepository.findBdasAprv(saveRequest.ptId, saveRequest.bdasSeq)
        val approvedBdasAprv = bdasAprvList?.filter { it.aprvYn == "Y" }
        val asgnReqSeqList = mutableListOf(saveRequest.asgnReqSeq)

        bdasAprvList?.let {
            asgnReqSeqList.addAll(it.map { bdasAprv -> bdasAprv.id.asgnReqSeq })
        }

        // 거절한 병원의 정보 저장
        if (saveRequest.aprvYn == "N") {
            bdasAprvRepository.getEntityManager().merge(saveRequest.toRefuseEntity(null, null))
            return CommonResponse(BdasAprvResponse(false, "배정 불가 처리되었습니다."))
        }

        // 이미 승인한 병원이 있는지 확인
        if (!approvedBdasAprv.isNullOrEmpty()) {
            return CommonResponse(BdasAprvResponse(true, "이미 승인한 병원이 존재합니다. 자동으로 배정 불가 처리되었습니다."))
        }

        // 승인한 병원의 정보 저장 및 나머지 병원 거절 + push 알림?
        bdasAprvRepository.persist(saveRequest.toApproveEntity())
        bdasReqAprvList.filter { it.id.asgnReqSeq !in asgnReqSeqList }.forEach {
            bdasAprvRepository.persist(it.convertToRefuseBdasAprv())
        }
        findBdasReq.changeBedStatTo(BedStatCd.BAST0005.name)

        // TODO
        // 모든 병원이 배정불가인 경우
        val refusedBdasAprv = bdasAprvRepository.findRefusedBdasAprv(saveRequest.ptId, saveRequest.bdasSeq)
        if (bdasReqAprvList.size == refusedBdasAprv.size) {
            findBdasReq.changeBedStatTo(BedStatCd.BAST0008.name)
//            firebaseService.sendMessage(entity.rgstUserId!!, "${saveRequest.msg}", "TEST-APR-1")
        }

        return CommonResponse(BdasAprvResponse(false, "배정 승인되었습니다."))
    }

    @Transactional
    fun confirmTrans(saveRequest: BdasTrnsSaveRequest): CommonResponse<String> {
        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(saveRequest.ptId, saveRequest.bdasSeq) ?: throw NotFoundException("bdasReq not found")
        val bdasAprvList = bdasAprvRepository.findBdasAprvList(saveRequest.ptId, saveRequest.bdasSeq)
        if (bdasAprvList.isEmpty()) {
            throw CustomizedException("의료진 승인 정보가 없습니다.", Response.Status.BAD_REQUEST)
        }

        bdasTrnsRepository.persist(saveRequest.toEntity())
        findBdasReq.changeBedStatTo(BedStatCd.BAST0006.name)

        return CommonResponse("이송 정보 등록 성공")
    }

    @Transactional
    fun confirmHosp(saveRequest: BdasAdmsSaveRequest): CommonResponse<String> {
        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(saveRequest.ptId, saveRequest.bdasSeq) ?: throw NotFoundException("bdasReq not found")
        val findBdasAdms = bdasAdmsRepository.findByIdOrderByAdmsSeqDesc(saveRequest.ptId, saveRequest.bdasSeq)

        val entity = if (findBdasAdms == null) {
            saveRequest.toEntity(saveRequest.admsStatCd, 1)
        } else {
            if (findBdasAdms.isAdmsStatCdDuplicate(saveRequest.admsStatCd)) {
                throw CustomizedException("입/퇴원 상태(admsStatCd) 중복입니다.", Response.Status.BAD_REQUEST)
            }
            saveRequest.toEntity(saveRequest.admsStatCd, findBdasAdms.id.admsSeq + 1)
        }

        bdasAdmsRepository.persist(entity)
        findBdasReq.changeBedStatTo(BedStatCd.BAST0007.name)

        return CommonResponse("입퇴원 정보 등록 성공")
    }

    @Transactional
    fun getBedAsgnList(): CommonResponse<*> {
        val bdasReqList = mutableListOf<BdasListDto>()
        val bdasReqAprvList = mutableListOf<BdasListDto>()
        val bdasAprvList = mutableListOf<BdasListDto>()
        val bdasTrnsList = mutableListOf<BdasListDto>()
        val bdasAdmsList = mutableListOf<BdasListDto>()

        val bdasReqMap = mutableMapOf("count" to 0, "items" to Collections.EMPTY_LIST)
        val bdasReqAprvMap = mutableMapOf("count" to 0, "items" to Collections.EMPTY_LIST)
        val bdasAprvMap = mutableMapOf("count" to 0, "items" to Collections.EMPTY_LIST)
        val bdasTrnsMap = mutableMapOf("count" to 0, "items" to Collections.EMPTY_LIST)
        val bdasAdmsMap = mutableMapOf("count" to 0, "items" to Collections.EMPTY_LIST)

        val findBdasList = bdasReqRepository.findBdasList()
        findBdasList.forEach {
            when (it.bedStatCd) {
                BedStatCd.BAST0003.name -> {
                    bdasReqList.add(it)
                    makeToResultMap(bdasReqList, bdasReqMap)
                }
                BedStatCd.BAST0004.name -> {
                    bdasReqAprvList.add(it)
                    makeToResultMap(bdasReqAprvList, bdasReqAprvMap)
                }
                BedStatCd.BAST0005.name -> {
                    bdasAprvList.add(it)
                    makeToResultMap(bdasAprvList, bdasAprvMap)
                }
                BedStatCd.BAST0006.name -> {
                    bdasTrnsList.add(it)
                    makeToResultMap(bdasTrnsList, bdasTrnsMap)
                }
                BedStatCd.BAST0007.name -> {
                    bdasAdmsList.add(it)
                    makeToResultMap(bdasAdmsList, bdasAdmsMap)
                }
                BedStatCd.BAST0008.name -> {
                    bdasAdmsList.add(it)
                    makeToResultMap(bdasAdmsList, bdasAdmsMap)
                }
            }
        }

        val res = listOf(bdasReqMap, bdasReqAprvMap, bdasAprvMap, bdasTrnsMap, bdasAdmsMap)

        return CommonResponse(res)
    }

    @Transactional
    fun getTimeLine(ptId: String, bdasSeq: Int): CommonResponse<*> {
        val bedStatCd = bdasReqRepository.findBedStat(ptId, bdasSeq) ?: throw NotFoundException("병상배정 정보가 없습니다.")
        val timeLineList = mutableListOf<BdasTimeLineDto>()

        log.debug(bedStatCd)
        val closedBdasAprv = BdasTimeLineDto("병상배정", TimeLineStatCd.CLOSED.cdNm)
        val closedBdasTrans = BdasTimeLineDto("이송", TimeLineStatCd.CLOSED.cdNm)
        val closedBdasAdms = BdasTimeLineDto("입원", TimeLineStatCd.CLOSED.cdNm)

        when (bedStatCd) {
            BedStatCd.BAST0003.name -> {
                val list = bdasReqRepository.findTimeLineInfo(ptId, bdasSeq)
                timeLineList.addAll(list)
                timeLineList.add(BdasTimeLineDto("승인대기", list[0].assignInstNm, TimeLineStatCd.SUSPEND.cdNm))
                timeLineList.addAll(mutableListOf(closedBdasAprv, closedBdasTrans, closedBdasAdms))
            }
            BedStatCd.BAST0004.name -> {
                timeLineList.addAll(bdasReqRepository.findTimeLineInfo(ptId, bdasSeq))
                timeLineList.addAll(bdasReqAprvRepository.findTimeLineInfo(ptId, bdasSeq))
                timeLineList.addAll(bdasAprvRepository.findTimeLineInfo(ptId, bdasSeq))
                timeLineList.add(closedBdasTrans)
                timeLineList.add(closedBdasAdms)
            }
            BedStatCd.BAST0005.name -> {
                timeLineList.addAll(bdasReqRepository.findTimeLineInfo(ptId, bdasSeq))
                timeLineList.addAll(bdasReqAprvRepository.findTimeLineInfo(ptId, bdasSeq))
                timeLineList.addAll(bdasAprvRepository.findTimeLineInfo(ptId, bdasSeq))
                timeLineList.add(BdasTimeLineDto("이송대기", TimeLineStatCd.SUSPEND.cdNm))
                timeLineList.add(closedBdasAdms)
            }
            BedStatCd.BAST0006.name -> {
                timeLineList.addAll(bdasReqRepository.findTimeLineInfo(ptId, bdasSeq))
                timeLineList.addAll(bdasReqAprvRepository.findTimeLineInfo(ptId, bdasSeq))
                timeLineList.addAll(bdasAprvRepository.findTimeLineInfo(ptId, bdasSeq))
                timeLineList.addAll(bdasTrnsRepository.findTimeLineInfo(ptId, bdasSeq))
                timeLineList.add(closedBdasAdms)
            }
            BedStatCd.BAST0007.name -> {
                timeLineList.addAll(bdasReqRepository.findTimeLineInfo(ptId, bdasSeq))
                timeLineList.addAll(bdasReqAprvRepository.findTimeLineInfo(ptId, bdasSeq))
                timeLineList.addAll(bdasAprvRepository.findTimeLineInfo(ptId, bdasSeq))
                timeLineList.addAll(bdasTrnsRepository.findTimeLineInfo(ptId, bdasSeq))
                timeLineList.addAll(bdasAdmsRepository.findTimeLineInfo(ptId, bdasSeq))
            }
        }
        return CommonResponse(TimeLineDtoList(timeLineList.size, timeLineList))
    }

    @Transactional
    fun getDiseaseInfo(ptId: String): CommonResponse<*> {
        val findEsvy = bdasEsvyRepository.findByPtIdWithLatestBdasSeq(ptId)
        val findReq = bdasReqRepository.findByPtId(ptId)
        bdasReqRepository.getEntityManager().detach(findReq)
        findReq?.ptTypeCd = convertFromArr(findReq?.ptTypeCd, "PTTP")
        findReq?.undrDsesCd = convertFromArr(findReq?.undrDsesCd, "UDDS")
        findReq?.svrtTypeCd = convertFromArr(findReq?.svrtTypeCd, "SVTP")
        findReq?.dnrAgreYn = baseCodeRepository.getCdNm("DNRA", findReq?.dnrAgreYn!!)
        findReq.reqBedTypeCd = baseCodeRepository.getCdNm("BDTP", findReq.reqBedTypeCd)

        return CommonResponse(DiseaseInfoResponse(findEsvy, findReq))
    }

    private fun makeToResultMap(list: MutableList<BdasListDto>, map: MutableMap<String, Any>) {
        map["count"] = list.size
        map["items"] = list
    }

    private fun convertFromArr(beforeConvert: String?, grpCd: String) : String {
        val convertArr = beforeConvert?.split(";")?.toMutableList() ?: mutableListOf()
        log.warn(convertArr.size)
        var result = ""

        convertArr.forEachIndexed{ index, item ->
            convertArr[index] = baseCodeRepository.getCdNm(grpCd, item)
            result += if(index == convertArr.size-1) convertArr[index] else "${convertArr[index]};"
        }
        log.warn(result)

        return result
    }

    // Haversine formula
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val lat1Rad = Math.toRadians(lat1)
        val lon1Rad = Math.toRadians(lon1)
        val lat2Rad = Math.toRadians(lat2)
        val lon2Rad = Math.toRadians(lon2)
        val earthRadius = 6371.0 //Kilometers
        return earthRadius * acos(sin(lat1Rad) * sin(lat2Rad) + cos(lat1Rad) * cos(lat2Rad) * cos(lon1Rad - lon2Rad))
    }

    private fun convertToStringDistance(result: Double) = when {
        result < 1.000 -> "${(result * 1000.0).roundToInt()}m"
        result >= 1.000 -> "${(result * 100.0).roundToInt() / 100.0}km"
        else -> ""
    }
}