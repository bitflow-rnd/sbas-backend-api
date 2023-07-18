package org.sbas.services

import org.jboss.logging.Logger
import org.sbas.constants.enums.*
import org.sbas.dtos.bdas.*
import org.sbas.entities.bdas.BdasReq
import org.sbas.entities.bdas.BdasReqId
import org.sbas.handlers.GeocodingHandler
import org.sbas.repositories.*
import org.sbas.responses.CommonResponse
import org.sbas.responses.patient.DiseaseInfoResponse
import org.sbas.restclients.FirebaseService
import org.sbas.restparameters.NaverGeocodingApiParams
import org.sbas.utils.CustomizedException
import org.sbas.utils.StringUtils
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.BadRequestException
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
    fun regDisesInfo(bdasEsvyDto: BdasEsvyDto): CommonResponse<String> {
        // 환자 정보 저장
        val findInfoPt = infoPtRepository.findById(bdasEsvyDto.ptId) ?: throw NotFoundException("${bdasEsvyDto.ptId} not found")
        bdasEsvyDto.saveInfoPt(findInfoPt)

        log. debug("regDisesInfo >>>>> ${bdasEsvyDto.ptId}")
//        val findBdasEsvy = bdasEsvyRepository.findByPtIdWithLatestBdasSeq(findInfoPt.id!!)
//        if (findBdasEsvy != null) { // 수정하는 경우
//            findBdasEsvy.bdasSeq
//        } else { // 처음 등록일 경우
//            log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
//            val bdasEsvy = bdasEsvyDto.toEntity()
//            bdasEsvyRepository.persist(bdasEsvy)
//            log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
//            res["bdasSeq"] = bdasEsvy.bdasSeq!!
//        }

        val bdasEsvy = bdasEsvyDto.toEntity()
        bdasEsvyRepository.persist(bdasEsvy)
        
        return CommonResponse("등록 성공")
    }

    /**
     * 중증도 분류 정보 등록
     */
    @Transactional
    fun regBioInfo(bdasReqSvrInfo: BdasReqSvrInfo): CommonResponse<String> {
        // bdasEsvy 에서 bdasSeq 가져오기
        val bdasEsvy = bdasEsvyRepository.findByPtIdWithLatestBdasSeq(bdasReqSvrInfo.ptId) ?: throw NotFoundException("${bdasReqSvrInfo.ptId} not found")

//        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(bdasEsvy.ptId, bdasEsvy.bdasSeq!!)
//        if (findBdasReq != null) { // 수정하는 경우
//        } else { // 새로 저장
//        }

        // 엔티티 새로 생성 후 persist
        val bdasReqId = BdasReqId(bdasEsvy.ptId, bdasEsvy.bdasSeq)
        val bdasReq = BdasReq.createDefault(bdasReqId)

        bdasReqRepository.persist(bdasReq)
        
        // 중증도 분류 정보 저장
        bdasReq.saveBioInfoFrom(bdasReqSvrInfo)

        return CommonResponse("등록 성공")
    }

    /**
     * 중증 정보 등록
     */
    @Transactional
    fun regServInfo(bdasReqSvrInfo: BdasReqSvrInfo): CommonResponse<String> {
        // bdasEsvy 에서 bdasSeq 가져오기
        val bdasEsvy = bdasEsvyRepository.findByPtIdWithLatestBdasSeq(bdasReqSvrInfo.ptId) ?: throw NotFoundException("${bdasReqSvrInfo.ptId} not found")

        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(bdasReqSvrInfo.ptId, bdasEsvy.bdasSeq!!)
        if (findBdasReq != null) { // 중증도 분류 정보 등록 후 넘어오는 경우
            // 기존 bdasReq 엔티티에 SvrInfo 저장
            findBdasReq.saveSvrInfoFrom(bdasReqSvrInfo)
        } else { // 새로 저장
            // 엔티티 새로 생성 후 persist
            val bdasReqId = BdasReqId(bdasReqSvrInfo.ptId, bdasEsvy.bdasSeq!!)
            val bdasReq = BdasReq.createDefault(bdasReqId)

            bdasReqRepository.persist(bdasReq)
            
            // 중증 정보 저장
            bdasReq.saveSvrInfoFrom(bdasReqSvrInfo)
        }

        return CommonResponse("등록 성공")
    }

    /**
     * 출발지 정보 등록
     */
    @Transactional
    fun regstrtpoint(bdasReqDprtInfo: BdasReqDprtInfo): CommonResponse<String> {
        // bdasEsvy 에서 bdasSeq 가져오기
        val bdasEsvy = bdasEsvyRepository.findByPtIdWithLatestBdasSeq(bdasReqDprtInfo.ptId) ?: throw NotFoundException("${bdasReqDprtInfo.ptId} not found")
        log.debug(">>>>>>>>>>>>${bdasEsvy.bdasSeq}")

        // 저장되어 있는 bdasReq
        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(bdasReqDprtInfo.ptId, bdasEsvy.bdasSeq!!) ?: throw NotFoundException("병상 배정 요청 정보가 없습니다.")

        // 출발지 위도, 경도 설정
        val geocoding = geoHandler.getGeocoding(NaverGeocodingApiParams(query = bdasReqDprtInfo.dprtDstrBascAddr!!))
        bdasReqDprtInfo.dprtDstrLat = geocoding.addresses!![0].y // 위도
        bdasReqDprtInfo.dprtDstrLon = geocoding.addresses!![0].x // 경도

        // 요청 시간 설정
        bdasReqDprtInfo.reqDt = StringUtils.getYyyyMmDd()
        bdasReqDprtInfo.reqTm = StringUtils.getHhMmSs()
        
        // 출발지 정보 저장
        findBdasReq.saveDprtInfoFrom(bdasReqDprtInfo)
        
        // infoPt 상태 변경
        val infoPt = infoPtRepository.findById(bdasReqDprtInfo.ptId)
        infoPt!!.changeBedStatAfterBdasReq()

        val bdasUsers =
            infoUserRepository.findBdasUserByReqDstrCd(findBdasReq.reqDstr1Cd, findBdasReq.reqDstr2Cd)

        // 푸쉬 알람 보내기
        bdasUsers.forEach {
            firebaseService.sendMessage(findBdasReq.rgstUserId!!, "새로운 병상배정 요청이 도착했습니다.", it.id!!)
        }

        return CommonResponse("등록 성공")
    }

    /**
     * 배정반 승인
     */
    @Transactional
    fun reqConfirm(dto: BdasReqAprvDto): CommonResponse<String> {
        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(dto.ptId, dto.bdasSeq) ?: throw NotFoundException("bdasReq not found")
        // 배정반 승인/거절
        if (dto.aprvYn == "N") { // 거절할 경우 거절 사유 및 메시지 작성
            bdasReqAprvRepository.persist(dto.toRefuseEntity())
        } else if (dto.aprvYn == "Y") { // 승인할 경우 원내 배정 여부 체크
            if (findBdasReq.inhpAsgnYn == "N") {
                // 전원 요청시 병원 정보 저장
                val hospList = infoHospRepository.findByHospIdList(dto.reqHospIdList)
                hospList.forEachIndexed { idx, infoHosp ->
                    log.debug("hospList>>>>>>>>>>> ${infoHosp.hospId}")
                    bdasReqAprvRepository.persist(dto.toEntityWhenNotInHosp(
                        asgnReqSeq = idx + 1,
                        hospId = infoHosp.hospId!!,
                        hospNm = infoHosp.dutyName!!,
                    ))
                }
                firebaseService.sendMessage("jiseongtak", "테스트입니다.", "jiseongtak")
            } else if (findBdasReq.inhpAsgnYn == "Y") {
                // 원내 배정이면 승인
                bdasReqAprvRepository.persist(dto.toEntityWhenInHosp())
            }
        }
        return CommonResponse("성공")
    }

    /**
     * 가용 병원 목록 조회
     */
    @Transactional
    fun getAvalHospList(ptId: String, bdasSeq: Int): CommonResponse<*> {
        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(ptId, bdasSeq) ?: throw NotFoundException("bdasReq not found")

        // dstrCd1, dstrCd2 구하기
        val splitAddress = findBdasReq.dprtDstrBascAddr!!.split(" ")
        val sido = splitAddress[0]
        val siGunGu = splitAddress[1]

        val dstrCd1 = StringUtils.getDstrCd1(sido)
        val findBaseCode = baseCodeRepository.findByDstr1CdAndCdNm(dstrCd1, siGunGu) ?: throw NotFoundException("baseCode not found")
        
        // dstrCd1, dstrCd2에 해당하는 infoHosp 목록
        val infoHospList = infoHospRepository.findListByDstrCd1AndDstrCd2(dstrCd1, findBaseCode.id.cdId)
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
        val sortedList = list.subList(0, 10).sortedBy { it.doubleDistance }
        return CommonResponse(mutableMapOf("count" to sortedList.size, "items" to sortedList))
    }

    /**
     * 의료진 승인
     */
    @Transactional
    fun asgnConfirm(dto: BdasAprvDto): CommonResponse<BdasAprvResponse> {
        // TODO 몇가지 다른 경우 고려
        val bdasReqAprvList = bdasReqAprvRepository.findReqAprvList(dto.ptId, dto.bdasSeq)
        val bdasAprvList = bdasAprvRepository.findBdasAprv(dto.ptId, dto.bdasSeq)
        val approvedBdasAprv = bdasAprvList?.filter { it.aprvYn == "Y" }
        val asgnReqSeqList = mutableListOf(dto.asgnReqSeq)

        bdasAprvList?.let {
            asgnReqSeqList.addAll(it.mapNotNull { bdasAprv -> bdasAprv.id!!.asgnReqSeq })
        }

        // 거절한 병원의 정보 저장
        if (dto.aprvYn == "N") {
            bdasAprvRepository.getEntityManager().merge(dto.toRefuseEntity(null, null))
            return CommonResponse(BdasAprvResponse(false, "배정 불가 처리되었습니다."))
        }

        // 이미 승인한 병원이 있는지 확인
        if (!approvedBdasAprv.isNullOrEmpty()) {
            return CommonResponse(BdasAprvResponse(true, "이미 승인한 병원이 존재합니다. 자동으로 배정 불가 처리되었습니다."))
        }

        // 승인한 병원의 정보 저장 및 나머지 병원 거절 + push 알림?
        bdasAprvRepository.persist(dto.toApproveEntity())
        bdasReqAprvList.filter { it.id?.asgnReqSeq !in asgnReqSeqList }.forEach {
            bdasAprvRepository.persist(it.convertToBdasAprv())
        }

        return CommonResponse(BdasAprvResponse(false, "배정 승인되었습니다."))
    }

    @Transactional
    fun confirmTrans(dto: BdasTrnsSaveDto): CommonResponse<String> {
        bdasTrnsRepository.persist(dto.toEntity())
        return CommonResponse("등록 성공")
    }

    @Transactional
    fun confirmHosp(dto: BdasAdmsSaveDto): CommonResponse<String> {
        val findBdasAdms = bdasAdmsRepository.findByIdOrderByAdmsSeqDesc(dto.ptId, dto.bdasSeq)

        val entity = if (findBdasAdms == null) {
            dto.toEntity(dto.admsStatCd, 1)
        } else {
            if (findBdasAdms.isAdmsStatCdDuplicate(dto.admsStatCd)) {
                throw CustomizedException("입/퇴원 상태(admsStatCd) 중복입니다.", Response.Status.BAD_REQUEST)
            }
            dto.toEntity(dto.admsStatCd, findBdasAdms.id.admsSeq + 1)
        }
        bdasAdmsRepository.persist(entity)

        return CommonResponse("success ${entity.id}")
    }

    @Transactional
    fun getBedAsgnList(): CommonResponse<*> {
        val bdasReqList = mutableListOf<BdasListDto>()
        val bdasReqAprvList = mutableListOf<BdasListDto>()
        val bdasAprvList = mutableListOf<BdasListDto>()
        val transferList = mutableListOf<BdasListDto>()
        val hospitalList = mutableListOf<BdasListDto>()

        val bdasReqMap = mutableMapOf("count" to 0, "items" to Collections.EMPTY_LIST)
        val bdasReqAprvMap = mutableMapOf("count" to 0, "items" to Collections.EMPTY_LIST)
        val bdasAprvMap = mutableMapOf("count" to 0, "items" to Collections.EMPTY_LIST)
        val transfer = mutableMapOf("count" to 0, "items" to Collections.EMPTY_LIST)
        val hospital = mutableMapOf("count" to 0, "items" to Collections.EMPTY_LIST)

        val findBdasList = bdasReqRepository.findBdasList()
        findBdasList.forEach {
            when (it.bedStatCd) {
                BedStatCd.BAST0003.name -> {
                    bdasReqList.add(getTagList(it))
                    makeToResultMap(bdasReqList, bdasReqMap)
                }
                BedStatCd.BAST0004.name -> {
                    bdasReqAprvList.add(getTagList(it))
                    makeToResultMap(bdasReqAprvList, bdasReqAprvMap)
                }
                BedStatCd.BAST0005.name -> {
                    bdasAprvList.add(getTagList(it))
                    makeToResultMap(bdasAprvList, bdasAprvMap)
                }
                BedStatCd.BAST0006.name -> {
                    transferList.add(getTagList(it))
                    makeToResultMap(transferList, transfer)
                }
                BedStatCd.BAST0007.name -> {
                    hospitalList.add(getTagList(it))
                    makeToResultMap(hospitalList, hospital)
                }
            }
        }

        val res = listOf(bdasReqMap, bdasReqAprvMap, bdasAprvMap, transfer, hospital)

        return CommonResponse(res)
    }

    @Transactional
    fun getTimeLine(ptId: String, bdasSeq: Int): CommonResponse<*> {
        val bedStatCd = bdasReqRepository.findBedStat(ptId, bdasSeq)
        val timeLineList = mutableListOf<BdasTimeLineDto>()

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
        findReq.reqBedTypeCd = baseCodeRepository.getCdNm("BDTP", findReq.reqBedTypeCd!!)

        return CommonResponse(DiseaseInfoResponse(findEsvy, findReq))
    }

    private fun makeToResultMap(list: MutableList<BdasListDto>, map: MutableMap<String, Any>) {
        map["count"] = list.size
        map["items"] = list
    }

    private fun getTagList(dto: BdasListDto): BdasListDto {
        dto.bedStatCdNm = BedStatCd.valueOf(dto.bedStatCd!!).cdNm
        if (dto.ptTypeCd != null) {
            val split = dto.ptTypeCd!!.split(";")
            dto.tagList!!.addAll(split.map { PtTypeCd.valueOf(it).cdNm })
        }
        if (dto.svrtTypeCd != null) {
            val split = dto.svrtTypeCd!!.split(";")
            dto.tagList!!.addAll(split.map { SvrtTypeCd.valueOf(it).cdNm })
        }
        if (dto.undrDsesCd != null) {
            val split = dto.undrDsesCd!!.split(";")
            dto.tagList!!.addAll(split.map { UndrDsesCd.valueOf(it).cdNm })
        }
        return dto
    }

    private fun convertFromArr(beforeConvert: String?, grpCd: String) : String? {
        var convertArr = beforeConvert?.split(";")?.toMutableList() ?: mutableListOf()
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

    private fun createDefaultBdasReq(bdasReqId: BdasReqId): BdasReq {
        return BdasReq.createDefault(bdasReqId)
    }
}