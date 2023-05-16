package org.sbas.services

import org.jboss.logging.Logger
import org.sbas.constants.BedStat
import org.sbas.constants.PtTypeCd
import org.sbas.constants.SvrtTypeCd
import org.sbas.constants.UndrDsesCd
import org.sbas.dtos.bdas.*
import org.sbas.entities.bdas.BdasReq
import org.sbas.entities.bdas.BdasReqId
import org.sbas.handlers.GeocodingHandler
import org.sbas.repositories.BdasEsvyRepository
import org.sbas.repositories.BdasReqRepository
import org.sbas.repositories.InfoPtRepository
import org.sbas.responses.CommonResponse
import org.sbas.restparameters.NaverGeocodingApiParams
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
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
    private lateinit var bdasReqRepository: BdasReqRepository

    @Inject
    private lateinit var infoPtRepository: InfoPtRepository

    @Inject
    private lateinit var geoHandler: GeocodingHandler

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
        bdasReqDprtInfo.reqDt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        bdasReqDprtInfo.reqTm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"))
        
        // 출발지 정보 저장
        findBdasReq.saveDprtInfoFrom(bdasReqDprtInfo)
        
        // infoPt 상태 변경
        val infoPt = infoPtRepository.findById(bdasReqDprtInfo.ptId)
        infoPt!!.changeBedStatAfterBdasReq()

        return CommonResponse("등록 성공")
    }
    
    @Transactional
    fun reqConfirm(bdasAprvDto: BdasAprvDto) {
        val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(bdasAprvDto.ptId, bdasAprvDto.bdasSeq) ?: throw NotFoundException("bdasReq not found")
        // 배정반 승인/거절
        if (bdasAprvDto.aprvYn == "N") {

        } else if (bdasAprvDto.aprvYn == "Y") {

        }


        if (findBdasReq.inhpAsgnYn == "N") {

        } else if (findBdasReq.inhpAsgnYn == "Y") {

        }

        // 거절할 경우 거절 사유 및 메시지 작성

        // 승인할 경우 원내 배정 여부 체크

        // 원내 배정이면 승인

        // 원내 배정 아니면 병원 선택

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
                BedStat.BAST0003.name -> {
                    bedRequestList.add(dto)
                    makeToResultMap(bedRequestList, bedRequest)
                }
                BedStat.BAST0005.name -> {
                    bedAssignList.add(dto)
                    makeToResultMap(bedAssignList, bedAssign)
                }
                BedStat.BAST0006.name -> {
                    transferList.add(dto)
                    makeToResultMap(transferList, transfer)
                }
                BedStat.BAST0007.name -> {
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




        return CommonResponse(bdasReqRepository.findTimeLineInfo(ptId, bdasSeq))



    }

    private fun makeToResultMap(list: MutableList<BdasListDto>, map: MutableMap<String, Any>) {
        list.map { getTagList(it) }
        map["count"] = list.size
        map["items"] = list
    }

    private fun getTagList(dto: BdasListDto): BdasListDto {
        dto.bedStatCdNm = BedStat.valueOf(dto.bedStatCd!!).cdNm
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
}