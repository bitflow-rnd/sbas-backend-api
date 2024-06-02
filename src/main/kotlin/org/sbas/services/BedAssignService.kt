package org.sbas.services

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.jwt.JsonWebToken
import org.jboss.logging.Logger
import org.sbas.component.InfoCrewComponent
import org.sbas.component.base.BaseCodeReader
import org.sbas.constants.enums.BedStatCd
import org.sbas.constants.enums.TimeLineStatCd
import org.sbas.dtos.bdas.*
import org.sbas.entities.bdas.BdasReqId
import org.sbas.handlers.GeocodingHandler
import org.sbas.repositories.*
import org.sbas.responses.CommonListResponse
import org.sbas.responses.CommonResponse
import org.sbas.responses.patient.DiseaseInfoResponse
import org.sbas.responses.patient.TransInfoResponse
import org.sbas.responses.patient.toTransInfoResponse
import org.sbas.restparameters.NaverGeocodingApiParams
import org.sbas.utils.CustomizedException
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

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
  private lateinit var bdasReqAprvRepository: BdasReqAprvRepository

  @Inject
  private lateinit var bdasAprvRepository: BdasAprvRepository

  @Inject
  private lateinit var bdasTrnsRepository: BdasTrnsRepository

  @Inject
  private lateinit var bdasAdmsRepository: BdasAdmsRepository

  @Inject
  private lateinit var infoPtRepository: InfoPtRepository

  @Inject
  private lateinit var infoHospRepository: InfoHospRepository

  @Inject
  private lateinit var infoUserRepository: InfoUserRepository

  @Inject
  private lateinit var activityHistoryRepository: UserActivityHistoryRepository

  @Inject
  private lateinit var geoHandler: GeocodingHandler

  @Inject
  private lateinit var firebaseService: FirebaseService

  @Inject
  private lateinit var infoCrewComponent: InfoCrewComponent

  @Inject
  private lateinit var baseCodeReader: BaseCodeReader

  @Inject
  private lateinit var jwt: JsonWebToken

  /**
   * 질병 정보 등록
   */
  @Transactional
  fun regDisesInfo(saveRequest: BdasEsvySaveRequest): CommonResponse<String> {
    // 환자 정보 저장
    val findInfoPt =
      infoPtRepository.findById(saveRequest.ptId) ?: throw NotFoundException("${saveRequest.ptId} not found")
    saveRequest.saveInfoPt(findInfoPt)

    log.debug("regDisesInfo >>>>> ${saveRequest.ptId}")
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
    val bdasEsvy = bdasEsvyRepository.findByPtIdWithLatestBdasSeq(ptId)
    val bdasReqId = BdasReqId(ptId, bdasEsvy.bdasSeq)

    // 출발지 위도, 경도 설정
    val geocoding = geoHandler.getGeocoding(NaverGeocodingApiParams(query = bdasReqDprtInfo.dprtDstrBascAddr))
    bdasReqDprtInfo.dprtDstrLat = geocoding.addresses!![0].y // 위도
    bdasReqDprtInfo.dprtDstrLon = geocoding.addresses!![0].x // 경도

    val bdasReq = saveRequest.toEntity(bdasReqId)
    bdasReqRepository.persist(bdasReq)

    val findInfoPt = infoPtRepository.findById(ptId) ?: throw NotFoundException("$ptId not found")

    // 지역코드로 병상배정반 찾기
    val bdasUsers = infoUserRepository.findBdasUserByReqDstrCd(bdasReq.reqDstr1Cd, bdasReq.reqDstr2Cd)

    // 푸쉬 알람 보내기
    firebaseService.sendMessageMultiDeviceV2("${findInfoPt.ptNm}님 병상요청", "신규 병상요청", bdasUsers.map { it.id })

    activityHistoryRepository.save(saveRequest.convertToActivityHistory(bdasReq.rgstUserId))

    return CommonResponse("병상 요청 성공")
  }

  /**
   * 배정반 승인
   */
  @Transactional
  fun reqConfirm(saveRequest: BdasReqAprvSaveRequest): CommonResponse<String> {

    val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(saveRequest.ptId, saveRequest.bdasSeq)
    val bdasReqAprvs = bdasReqAprvRepository.findAllByPtIdAndBdasSeq(saveRequest.ptId, saveRequest.bdasSeq)
    val requestUser = infoUserRepository.findByUserId(findBdasReq.rgstUserId)
      ?: throw NotFoundException("user not found")
    val findInfoPt =
      infoPtRepository.findById(saveRequest.ptId) ?: throw NotFoundException("${saveRequest.ptId} not found")

    // 배정반 승인/거절
    if (saveRequest.aprvYn == "N") { // 거절할 경우 거절 사유 및 메시지 작성

      bdasReqAprvRepository.persist(saveRequest.toRefuseEntity())
      findBdasReq.changeBedStatTo(BedStatCd.BAST0008.name)
      return CommonResponse("배정 불가 처리 완료")

    } else if (saveRequest.aprvYn == "Y") { // 승인할 경우 원내 배정 여부 체크

      val userIdList = emptyList<String>().toMutableList()

      if (findBdasReq.inhpAsgnYn == "N") {

        // 전원 요청시 병원 정보 저장
        val oldAsgnReqSeq = bdasReqAprvs.size
        val hospList = infoHospRepository.findByHospIdList(saveRequest.reqHospIdList)

        log.info("${saveRequest.reqHospIdList} / hospList / ${hospList.size}")

        hospList.forEachIndexed { idx, infoHosp ->
          val entity = saveRequest.toEntityWhenNotInHosp(
            asgnReqSeq = idx + 1 + oldAsgnReqSeq,
            hospId = infoHosp.hospId!!,
            hospNm = infoHosp.dutyName!!,
          )
          bdasReqAprvRepository.persist(entity)
          // userIdList.add(infoHosp.userId) // 병상 승인이 안되서 쿼리 단순화 하고 임시 주석처리
        }

        try {
          firebaseService.sendMessageMultiDeviceV2(
            "${findInfoPt.ptNm}님 전원요청",
            "가용 병상 확인해 주시기 바랍니다.",
            userIdList
          )
        } catch (e: Exception) {
          log.error("[BED-ASGN] ERR " + e.message)
          e.printStackTrace()
        }

      } else if (findBdasReq.inhpAsgnYn == "Y") { // 원내 배정 승인
        // TODO reqHospId
        bdasReqAprvRepository.persist(saveRequest.toEntityWhenInHosp(requestUser.instId, requestUser.instNm))
      }

      findBdasReq.changeBedStatTo(BedStatCd.BAST0004.name)

    } else {
      throw CustomizedException("aprvYn 값이 올바르지 않습니다.", Response.Status.INTERNAL_SERVER_ERROR)
    }

    activityHistoryRepository.save(saveRequest.convertToActivityHistory(jwt.name))

    return CommonResponse("승인 성공")
  }

  /**
   * 가용 병원 목록 조회
   */
  @Transactional
  fun getAvalHospList(ptId: String, bdasSeq: Int): CommonResponse<*> {
    val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(ptId, bdasSeq)

    val dstr1Cd = findBdasReq.reqDstr1Cd
    val dstr2Cd: String? = findBdasReq.reqDstr2Cd

    // 병상 배정 요청시 선택한 dstr1Cd, dstr2Cd에 해당하는 infoHosp 목록
    val infoHospList = infoHospRepository.findAvalHospListBydstr1Cd(dstr1Cd, dstr2Cd)
    log.debug("getAvalHospList >>>>>>>>>>>>>> ${infoHospList.size}")
    val list = infoHospList.map { avalHospDto ->
      val distance = calculateDistance(
        lat1 = findBdasReq.dprtDstrLat!!.toDouble(),
        lon1 = findBdasReq.dprtDstrLon!!.toDouble(),
        lat2 = avalHospDto.wgs84Lat!!.toDouble(),
        lon2 = avalHospDto.wgs84Lon!!.toDouble(),
      )
      AvalHospListResponse(
        hospId = avalHospDto.hospId,
        hospNm = avalHospDto.dutyName!!,
        doubleDistance = distance,
        distance = convertToStringDistance(distance),
        addr = avalHospDto.dutyAddr!!,
        gnbdIcu = avalHospDto.gnbdIcu, // hv22 54
        npidIcu = avalHospDto.npidIcu, // hv23 55
        gnbdSvrt = avalHospDto.gnbdSvrt, // hv24 56
        gnbdSmsv = avalHospDto.gnbdSmsv, // hv25
        gnbdModr = avalHospDto.gnbdModr, // hv26
        ventilator = avalHospDto.ventilator,
        ventilatorPreemie = avalHospDto.ventilatorPreemie,
        incubator = avalHospDto.incubator,
        ecmo = avalHospDto.ecmo,
        highPressureOxygen = avalHospDto.highPressureOxygen,
        ct = avalHospDto.ct,
        mri = avalHospDto.mri,
        bloodVesselImaging = avalHospDto.bloodVesselImaging,
        bodyTemperatureControl = avalHospDto.bodyTemperatureControl,
      )
      //        }.filter { response -> response.hospId !in findBdasReqAprv.map { it.reqHospId } }
    }

    //        // TODO 페이징 처리??
    //        if (list.size > 10) {
    ////            val sortedList = list.subList(0, 10).sortedByDescending { it.npidIcu }.sortedBy { it.doubleDistance }.distinctBy { it.hospId }
    //            val sortedList = list.subList(0, 10)
    //                .sortedWith(compareBy({ -it.npidIcu }, { -it.gnbdIcu }, { -it.gnbdSvrt }, { it.doubleDistance }))
    //                .distinctBy { it.hospId }
    //            return CommonListResponse(sortedList)
    //        }

    val sortedList =
      list.sortedWith(compareBy({ -it.npidIcu }, { -it.gnbdIcu }, { -it.gnbdSvrt }, { it.doubleDistance }))
        .distinctBy { it.hospId }.distinctBy { it.hospId }

    return CommonListResponse(sortedList)
  }

  /**
   * 의료진 승인
   */
  @Transactional
  fun asgnConfirm(saveRequest: BdasAprvSaveRequest): CommonResponse<*> {

    val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(saveRequest.ptId, saveRequest.bdasSeq)
    val findInfoUser = infoUserRepository.findByUserId(jwt.name) ?: throw NotFoundException("user not found")
    val findInfoPt =
      infoPtRepository.findById(saveRequest.ptId) ?: throw NotFoundException("${saveRequest.ptId} not found")
    val bdasReqAprvs = bdasReqAprvRepository.findAllByPtIdAndBdasSeq(saveRequest.ptId, saveRequest.bdasSeq)

    if (bdasReqAprvs.isEmpty()) {
      throw CustomizedException("배정 승인 정보가 없습니다.", Response.Status.BAD_REQUEST)
    } else {
      if (saveRequest.hospId == "INST000000") {
        // Todo : 시연용 임시. 전산이 승인하는 경우. 아무 병원정보로 승인처리
      } else {
        bdasReqAprvs.firstOrNull {
          log.debug("${it.id.asgnReqSeq}/${saveRequest.asgnReqSeq} <=>> ${it.reqHospId}/${saveRequest.hospId}")
          it.id.asgnReqSeq == saveRequest.asgnReqSeq && it.reqHospId == saveRequest.hospId
        } ?: throw CustomizedException("배정 요청 순번(asgnReqSeq) 및 병원 ID(hospId)가 일치하지 않습니다.", Response.Status.BAD_REQUEST)
      }
    }

    // 원내배정
    if (findBdasReq.inhpAsgnYn == "Y") {
      bdasAprvRepository.persist(saveRequest.toApproveEntity(findInfoUser.instId))
      findBdasReq.changeBedStatTo(BedStatCd.BAST0006.name)
      return CommonResponse("원내배정 승인")
    }

    // 거절한 병원의 정보 저장
    if (saveRequest.aprvYn == "N") {
      val entity = saveRequest.toRefuseEntity(saveRequest.msg, saveRequest.negCd)
      bdasAprvRepository.persist(entity)

      // TODO 재요청?
      // 모든 병원이 배정불가인 경우
      val refusedBdasAprv = bdasAprvRepository.findRefusedBdasAprv(saveRequest.ptId, saveRequest.bdasSeq)
      if (bdasReqAprvs.size == refusedBdasAprv.size) {
        firebaseService.sendMessageMultiDevice(
          "${findInfoPt.ptNm}님 병상배정",
          "모든 병원이 배정 불가 처리되었습니다. 재요청 바랍니다.",
          bdasReqAprvs[0].rgstUserId
        )
        //            findBdasReq.changeBedStatTo(BedStatCd.BAST0008.name)
        findBdasReq.changeBedStatTo(BedStatCd.BAST0003.name)
      }
      return CommonResponse(BdasAprvResponse(false, "배정 불가 처리되었습니다."))
    }

    val bdasAprvList = bdasAprvRepository.findBdasAprv(saveRequest.ptId, saveRequest.bdasSeq)
    val approvedBdasAprv = bdasAprvList?.filter { it.aprvYn == "Y" }

    // 이미 승인한 병원이 있는지 확인
    if (!approvedBdasAprv.isNullOrEmpty()) {
      return CommonResponse(BdasAprvResponse(true, "이미 승인한 병원이 존재합니다. 자동으로 배정 불가 처리되었습니다."))
    }

    // 승인한 병원의 정보 저장
    bdasAprvRepository.persist(saveRequest.toApproveEntity(null))

    val asgnReqSeqList = mutableListOf(saveRequest.asgnReqSeq)
    bdasAprvList?.let {
      asgnReqSeqList.addAll(it.map { bdasAprv -> bdasAprv.id.asgnReqSeq })
    }

    // 나머지 병원 거절 + push 알림?
    bdasReqAprvs.filter { it.id.asgnReqSeq !in asgnReqSeqList }.forEach {
      bdasAprvRepository.persist(it.convertToRefuseBdasAprv())
    }

    findBdasReq.changeBedStatTo(BedStatCd.BAST0005.name)

    // 푸쉬 알람 보내기
    val msg = when (saveRequest.aprvYn) {
      "Y" -> "병상 배정이 승인되었습니다."
      else -> "병상 배정이 불가처리되었습니다."
    }

    try {
      firebaseService.sendMessageMultiDevice("${findInfoPt.ptNm}님 병상배정", msg, bdasReqAprvs[0].rgstUserId)
    } catch (e: Exception) {
      e.printStackTrace()
    }

    activityHistoryRepository.save(saveRequest.convertToActivityHistory(jwt.name))

    return CommonResponse(BdasAprvResponse(false, "배정 승인되었습니다."))
  }

  /**
   * 이송처리
   */
  @Transactional
  fun confirmTrans(saveRequest: BdasTrnsSaveRequest): CommonResponse<String> {
    val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(saveRequest.ptId, saveRequest.bdasSeq)
    val bdasAprvList = bdasAprvRepository.findBdasAprvList(saveRequest.ptId, saveRequest.bdasSeq)
    if (bdasAprvList.isEmpty()) {
      throw CustomizedException("의료진 승인 정보가 없습니다.", Response.Status.BAD_REQUEST)
    }
    bdasTrnsRepository.persist(saveRequest.toEntity())

    infoCrewComponent.saveInfoCrew(saveRequest.toInfoCrewSaveReqList(), saveRequest.instId)
    infoCrewComponent.saveVehicleInfo(saveRequest.instId, saveRequest.vecno)

    findBdasReq.changeBedStatTo(BedStatCd.BAST0006.name)

    return CommonResponse("이송 정보 등록 성공")
  }

  @Transactional
  fun confirmHosp(saveRequest: BdasAdmsSaveRequest): CommonResponse<String> {
    val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(saveRequest.ptId, saveRequest.bdasSeq)
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

    return CommonResponse("${entity.id.ptId} ${entity.id.bdasSeq} 입퇴원 정보 등록 성공")
  }

  @Transactional
  fun findBedAsgnList(param: BdasListSearchParam): Map<String, Any> {
    val bdasReqList = BdasList(title = "병상요청", items = mutableListOf())
    val bdasReqAprvList = BdasList(title = "병상배정", items = mutableListOf())
    val bdasAprvList = BdasList(title = "이송/배차", items = mutableListOf())
    val bdasTrnsList = BdasList(title = "입/퇴원", items = mutableListOf())
    val bdasAdmsList = BdasList(title = "완료", items = mutableListOf())

    val findBdasList = bdasReqRepository.findBdasList(param)
    findBdasList.forEach {
      it.ptTypeCdNm = baseCodeReader.getBaseCodeCdNm("PTTP", it.ptTypeCd)?.joinToString(";")
      it.svrtTypeCdNm = baseCodeReader.getBaseCodeCdNm("SVTP", it.svrtTypeCd)?.joinToString(";")
      it.undrDsesCdNm = baseCodeReader.getBaseCodeCdNm("UDDS", it.undrDsesCd)?.joinToString(";")

      when (it.bedStatCd) {
        BedStatCd.BAST0003.name -> {
          bdasReqList.items.add(it)
        }

        BedStatCd.BAST0004.name -> {
          bdasReqAprvList.items.add(it)
        }

        BedStatCd.BAST0005.name -> {
          bdasAprvList.items.add(it)
        }

        BedStatCd.BAST0006.name -> {
          bdasTrnsList.items.add(it)
        }

        BedStatCd.BAST0007.name, BedStatCd.BAST0008.name -> {
          bdasAdmsList.items.add(it)
        }
      }
    }

    val res: List<BdasList> = listOf(bdasReqList, bdasReqAprvList, bdasAprvList, bdasTrnsList, bdasAdmsList)
    val count = bdasReqRepository.countBdasList(param)

    // TODO
    return mapOf("code" to "00", "message" to "SUCCESS", "count" to count, "result" to res)
  }

  @Transactional
  fun findBedAsgnListForWeb(param: BdasListSearchParam): CommonListResponse<BdasListDto> {
    val findBdasList = bdasReqRepository.findBdasListForWeb(param)
    val count = bdasReqRepository.countBdasList(param)
    return CommonListResponse(findBdasList, count.toInt())
  }

  @Transactional
  fun getTimeLine(ptId: String, bdasSeq: Int): CommonResponse<*> {
    val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(ptId, bdasSeq)
    val bedStatCd = findBdasReq.bedStatCd
    val timeLineList = mutableListOf<TimeLine>()

    log.debug(bedStatCd)
    val closedBdasAprv = ClosedTimeLine("병상배정", TimeLineStatCd.CLOSED.cdNm)
    val closedBdasTrans = ClosedTimeLine("이송", TimeLineStatCd.CLOSED.cdNm)
    val closedBdasAdms = ClosedTimeLine("입원", TimeLineStatCd.CLOSED.cdNm)
    val bdasTransWait = ClosedTimeLine("이송대기", TimeLineStatCd.SUSPEND.cdNm)

    when (bedStatCd) {
      BedStatCd.BAST0003.name -> {
        timeLineList.addAll(bdasReqRepository.findTimeLineInfo(ptId, bdasSeq))
        timeLineList.addAll(bdasReqRepository.findSuspendTimeLineInfo(ptId, bdasSeq))
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
        timeLineList.add(bdasTransWait)
        timeLineList.add(closedBdasAdms)
      }

      BedStatCd.BAST0006.name -> {
        timeLineList.addAll(bdasReqRepository.findTimeLineInfo(ptId, bdasSeq))
        timeLineList.addAll(bdasReqAprvRepository.findTimeLineInfo(ptId, bdasSeq))
        timeLineList.addAll(bdasAprvRepository.findTimeLineInfo(ptId, bdasSeq))
        timeLineList.addAll(bdasTrnsRepository.findSuspendTimeLineInfo(ptId, bdasSeq))
        timeLineList.addAll(bdasAdmsRepository.findSuspendTimeLineInfo(ptId, bdasSeq))
      }

      BedStatCd.BAST0007.name -> {
        timeLineList.addAll(bdasReqRepository.findTimeLineInfo(ptId, bdasSeq))
        timeLineList.addAll(bdasReqAprvRepository.findTimeLineInfo(ptId, bdasSeq))
        timeLineList.addAll(bdasAprvRepository.findTimeLineInfo(ptId, bdasSeq))
        timeLineList.addAll(bdasTrnsRepository.findCompleteTimeLineInfo(ptId, bdasSeq))
        timeLineList.addAll(bdasAdmsRepository.findCompleteTimeLineInfo(ptId, bdasSeq))
      }

      BedStatCd.BAST0008.name -> {
        timeLineList.addAll(bdasReqRepository.findTimeLineInfo(ptId, bdasSeq))
        val elements = bdasReqAprvRepository.findTimeLineInfo(ptId, bdasSeq)
        timeLineList.addAll(elements)

        //                if (elements[0].title == "배정불가") {
        //                    timeLineList.add(closedBdasAprv)
        //                } else {
        //                    timeLineList.addAll(bdasAprvRepository.findRefuseTimeLineInfo(ptId, bdasSeq))
        //                }

        timeLineList.add(closedBdasTrans)
        timeLineList.add(closedBdasAdms)
      }
    }
    return CommonResponse(TimeLineList(findBdasReq.id.ptId, findBdasReq.id.bdasSeq, timeLineList.size, timeLineList))
  }

  @Transactional
  fun getDiseaseInfo(ptId: String): CommonResponse<*> {
    val bdasEsvy = bdasEsvyRepository.findByPtIdWithLatestBdasSeq(ptId)
    val findReq =
      bdasReqRepository.findByPtIdWithLatestBdasSeq(ptId) ?: throw NotFoundException("$ptId request not found")

    return CommonResponse(DiseaseInfoResponse(bdasEsvy, findReq))
  }

  @Transactional
  fun findTransInfo(ptId: String, bdasSeq: Int): CommonResponse<TransInfoResponse> {
    val findBdasReq = bdasReqRepository.findByPtIdAndBdasSeq(ptId, bdasSeq)
    val findBdasTrns = bdasTrnsRepository.findByPtIdAndBdasSeqWithNull(ptId, bdasSeq)
    val destinationInfo = bdasAprvRepository.findDestinationInfo(ptId, bdasSeq)

    val transInfoResponse = findBdasReq.toTransInfoResponse(findBdasTrns, destinationInfo)

    return CommonResponse(transInfoResponse)
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

  @Transactional
  fun removeData(ptId: String?, step: Int?): CommonResponse<String> {
    if (step != null) {
      when (step) {
        0 -> {
          infoPtRepository.delete("ptId = '$ptId'")
        }

        1 -> {
          bdasEsvyRepository.delete("ptId = '$ptId'")
          bdasReqRepository.delete("id.ptId = '$ptId'")
        }

        2 -> {
          bdasReqAprvRepository.delete("id.ptId = '$ptId'")
        }

        3 -> {
          bdasAprvRepository.delete("id.ptId = '$ptId'")
        }

        4 -> {
          bdasTrnsRepository.delete("id.ptId = '$ptId'")
        }

        5 -> {
          bdasAdmsRepository.delete("id.ptId = '$ptId'")
        }

        6 -> {
          bdasReqAprvRepository.delete("id.ptId = '$ptId'")
          bdasAprvRepository.delete("id.ptId = '$ptId'")
          bdasTrnsRepository.delete("id.ptId = '$ptId'")
          bdasAdmsRepository.delete("id.ptId = '$ptId'")
        }
      }
    }
    return CommonResponse("$ptId 정보 삭제")
  }
}