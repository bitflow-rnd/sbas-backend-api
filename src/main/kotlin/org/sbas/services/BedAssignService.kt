package org.sbas.services

import io.quarkus.cache.CacheManager
import org.jboss.logging.Logger
import org.sbas.constants.BedStatCd
import org.sbas.constants.PtTypeCd
import org.sbas.constants.SvrtTypeCd
import org.sbas.constants.UndrDsesCd
import org.sbas.dtos.bdas.*
import org.sbas.entities.bdas.BdasAdmsId
import org.sbas.entities.bdas.BdasReq
import org.sbas.entities.bdas.BdasReqId
import org.sbas.handlers.GeocodingHandler
import org.sbas.repositories.*
import org.sbas.responses.CommonResponse
import org.sbas.responses.patient.DiseaseInfoResponse
import org.sbas.restclients.FirebaseService
import org.sbas.restparameters.NaverGeocodingApiParams
import org.sbas.utils.StringUtils
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional
import javax.ws.rs.NotFoundException
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

/**
 * 병상배정 관련 서비스 클래스
 */
@ApplicationScoped
class BedAssignService(
    private var log: Logger,
    private var bdasEsvyRepository: BdasEsvyRepository,
    private var bdasReqRepository: BdasReqRepository,
    private var bdasAsgnAprvRepository: BdasAsgnAprvRepository,
    private var bdasAprvRepository: BdasAprvRepository,
    private var bdasTrnsRepository: BdasTrnsRepository,
    private var bdasAdmsRepository: BdasAdmsRepository,
    private var infoPtRepository: InfoPtRepository,
    private var infoHospRepository: InfoHospRepository,
    private var baseCodeRepository: BaseCodeRepository,
    private var geoHandler: GeocodingHandler,
    private var firebaseService: FirebaseService,
    private var cacheManager: CacheManager,
) {

    /**
     * 질병 정보 등록
     */
    @Transactional
    fun regDisesInfo(bdasEsvyDto: BdasEsvyDto): CommonResponse<String> {
        // 환자 정보 저장
        val findInfoPt = infoPtRepository.findById(bdasEsvyDto.ptId) ?: throw NotFoundException("${bdasEsvyDto.ptId} not found")
        bdasEsvyDto.saveInfoPt(findInfoPt)
        
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

        return CommonResponse("등록 성공")
    }
    
    @Transactional
    fun reqConfirm(dto: BdasAsgnAprvDto): CommonResponse<String> {
        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(dto.ptId, dto.bdasSeq) ?: throw NotFoundException("bdasReq not found")
        // 배정반 승인/거절
        if (dto.aprvYn == "N") { // 거절할 경우 거절 사유 및 메시지 작성
            bdasAsgnAprvRepository.persist(dto.toUnableEntity())
        } else if (dto.aprvYn == "Y") { // 승인할 경우 원내 배정 여부 체크

            if (findBdasReq.inhpAsgnYn == "N") {
                // 전원 요청시 병원 정보 저장
                val hospList = infoHospRepository.findByHospIdList(dto.reqHospIdList)
                hospList.forEachIndexed { idx, infoHosp ->
                    log.debug("hospList>>>>>>>>>>> ${infoHosp.hospId}")
                    bdasAsgnAprvRepository.persist(dto.toEntityWhenNotInHosp(
                        asgnReqSeq = idx + 1,
                        hospId = infoHosp.hospId!!,
                        hospNm = infoHosp.dutyName!!,
                    ))
                }
                firebaseService.sendMessage("jiseong12", "테스트입니다.", "jiseongtak")
            } else if (findBdasReq.inhpAsgnYn == "Y") {
                // 원내 배정이면 승인
                bdasAsgnAprvRepository.persist(dto.toEntityWhenInHosp())
            }

        }
        return CommonResponse("성공")
    }

    @Transactional
    fun asgnConfirm(bdasAprvDto: BdasAprvDto) {
        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(bdasAprvDto.ptId, bdasAprvDto.bdasSeq) ?: throw NotFoundException("bdasReq not found")
        if (bdasAprvDto.aprvYn == "N") {
            // 거절하면 상태 변경
            bdasAprvRepository.persist(bdasAprvDto.toUnableEntity())
        } else if (bdasAprvDto.aprvYn == "Y") {
            // 이미 승인한 병원이 있는지 확인
            bdasAsgnAprvRepository.findAlreadyAprvHosp(bdasAprvDto.ptId, bdasAprvDto.bdasSeq)

            bdasAprvRepository.persist(bdasAprvDto.toEntity())
            // 승인한 후에 나머지 병원들 상태 변경

        }
    }

    @Transactional
    fun getBedAsgnList(): CommonResponse<*> {
        val bedRequestList = mutableListOf<BdasListDto>()
        val bedAssignList = mutableListOf<BdasListDto>()
        val transferList = mutableListOf<BdasListDto>()
        val hospitalList = mutableListOf<BdasListDto>()

        val bedRequest = mutableMapOf("count" to 0, "items" to Collections.EMPTY_LIST)
        val bedAssign = mutableMapOf("count" to 0, "items" to Collections.EMPTY_LIST)
        val transfer = mutableMapOf("count" to 0, "items" to Collections.EMPTY_LIST)
        val hospital = mutableMapOf("count" to 0, "items" to Collections.EMPTY_LIST)
        val complete = mutableMapOf("count" to 0, "items" to Collections.EMPTY_LIST)

        bdasReqRepository.findBdasReqList().forEach {
            dto -> when (dto.bedStatCd) {
                BedStatCd.BAST0003.name -> {
                    bedRequestList.add(dto)
                    makeToResultMap(bedRequestList, bedRequest)
                }
                BedStatCd.BAST0005.name -> {
                    bedAssignList.add(dto)
                    makeToResultMap(bedAssignList, bedAssign)
                }
                BedStatCd.BAST0006.name -> {
                    transferList.add(dto)
                    makeToResultMap(transferList, transfer)
                }
                BedStatCd.BAST0007.name -> {
                    hospitalList.add(dto)
                    makeToResultMap(hospitalList, hospital)
                }
            }
        }
        val res = listOf(bedRequest, bedAssign, transfer, hospital, complete)

        return CommonResponse(res)
    }

    @Transactional
    fun getTimeLine(ptId: String, bdasSeq: Int): CommonResponse<*> {
        val bedStatCd = bdasReqRepository.findBedStat(ptId, bdasSeq)
        log.debug(bedStatCd)
        when (bedStatCd) {
            BedStatCd.BAST0003.name -> {
                val list = bdasReqRepository.findTimeLineInfo(ptId, bdasSeq)
                list.add(BdasTimeLineDto("승인대기", list[0].assignInstNm))
                return CommonResponse(TimeLineDtoList(list.size, list))
            }
            BedStatCd.BAST0004.name -> {
                val list = bdasReqRepository.findTimeLineInfo(ptId, bdasSeq)
                list.addAll(bdasAsgnAprvRepository.findTimeLineInfo(ptId, bdasSeq))

                return CommonResponse(TimeLineDtoList(list.size, list))
            }
            BedStatCd.BAST0005.name -> return CommonResponse(Collections.EMPTY_LIST)
        }
        return CommonResponse(Collections.EMPTY_LIST)
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

    @Transactional
    fun confirmTrans(dto: BdasTrnsSaveDto): CommonResponse<String> {
        bdasTrnsRepository.persist(dto.toEntity())
        return CommonResponse("등록 성공")
    }

    @Transactional
    fun confirmHosp(bdasAdmsSaveDto: BdasAdmsSaveDto) {
        val findBdasAdms = bdasAdmsRepository.findById(BdasAdmsId(bdasAdmsSaveDto.ptId, bdasAdmsSaveDto.bdasSeq)) ?: throw NotFoundException("bdasAdms not found")
        if (bdasAdmsRepository.isPersistent(findBdasAdms)) {
            // 기존에 있으면 update

        }
        bdasAdmsRepository.persist(bdasAdmsSaveDto.toAdmsEntity())
    }

    private fun makeToResultMap(list: MutableList<BdasListDto>, map: MutableMap<String, Any>) {
        list.map { getTagList(it) }
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
}