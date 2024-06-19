package org.sbas.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.json.JSONObject
import org.sbas.entities.svrt.SvrtAnly
import org.sbas.entities.svrt.SvrtAnlyId
import org.sbas.repositories.SvrtCollRepository
import org.sbas.repositories.SvrtPtRepository
import org.sbas.restclients.NubisonAiSeverityAnalysisRestClient
import org.sbas.restparameters.MntrInfo
import org.sbas.restparameters.NubisonAiSeverityAnalysisResponse
import org.sbas.restparameters.NubisonSvrtAnlyInputData
import org.sbas.restparameters.NubisonSvrtAnlyRequest
import org.sbas.services.SvrtService
import org.sbas.utils.StringUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ApplicationScoped
class NubisonAiSeverityAnalysisHandler {

  @Inject
  private lateinit var log: Logger
  
  @RestClient
  private lateinit var nubisonAiSeverityAnalysisRestClient: NubisonAiSeverityAnalysisRestClient

  @Inject
  private lateinit var svrtService: SvrtService

  @Inject
  private lateinit var svrtPtRepository: SvrtPtRepository

  @Inject
  private lateinit var svrtCollRepository: SvrtCollRepository

  @OptIn(ExperimentalStdlibApi::class)
  fun analyse(pid: String): NubisonAiSeverityAnalysisResponse? {

    val svrtCollList = svrtService.getSvrtCollByPidAndMsreDt(pid)?.sortedBy { it.id.msreDt }
    if (!svrtCollList.isNullOrEmpty()) {
      val requestData = svrtService.getSvrtRequestData(svrtCollList)
      val requestBody = """
            {
                "content_type": "pd",
                "inputs": [
                    {
                        "data": [
                            $requestData
                        ],
                        "datatype": "BYTES",
                        "name": "data",
                        "parameters": {
                            "content_type": "str"
                        },
                        "shape": [
                            ${requestData.length}
                        ]
                    }
                ]
            }
        """.replace("\n", "")
      val currentAnlySeq = (svrtService.getLastAnlySeqValue() ?: 0) + 1

      /**
       * Response format:
       * model_name: 모델명
       * model_version: 모델 버전
       * id: 요청 구분 값
       * parameters: None
       * outputs[]: 모델 추론 값 구조. +0일, +1일, +2일, +3일 추론 값이 리스트에 순서대로 들어있습니다.
       * name: row 날짜 기준. +0day | +1day | +2day | +3day
       * shape: 추론 값의 길이. min(<입력한 환자의 데이터 날짜 수>, 4)
       * datatype: 추론 값의 데이터 형. FP32
       * parameters: None 무시
       * data[]: 모델 추론 값 리스트. 입력한 값 날짜 순으로 출력. ex) 2022-03-21, 2022-03-22, 2022-03-22, 2022-03-24
       */
      val response = nubisonAiSeverityAnalysisRestClient.infer(requestBody)
      val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
      for ((requestRowIndex, value) in svrtCollList.withIndex()) {
        val svrtAnlyRow = SvrtAnly(
          SvrtAnlyId(
            ptId = value.id.ptId,
            hospId = value.id.hospId,
            rgstSeq = value.id.rgstSeq,
            msreDt = value.id.msreDt,
            collSeq = requestRowIndex + 1, // +1 is to start from 1, not from 0
            anlyDt = StringUtils.getYyyyMmDd(),
            anlySeq = currentAnlySeq
          ),
          pid = pid,
          collDt = value.rsltDt,
          collTm = value.rsltTm,
          anlyTm = StringUtils.getHhMmSs(),
          prdtDt = LocalDate.parse(value.id.msreDt, dateFormat)
            .format(dateFormat),
          svrtProbMean = "%.3f".format(response.outputs[0].data[requestRowIndex]),
          svrtProbStd = "%.3f".format(response.outputs[1].data[requestRowIndex])
        )
        svrtService.saveSvrtAnly(svrtAnlyRow)
        if (requestRowIndex == (svrtCollList.size - 1)) {
          for (plusIndex in 1..3) {
            val svrtAnlyRow = SvrtAnly(
              SvrtAnlyId(
                ptId = value.id.ptId,
                hospId = value.id.hospId,
                rgstSeq = value.id.rgstSeq,
                msreDt = value.id.msreDt,
                collSeq = requestRowIndex + plusIndex + 1, // +1 is to start from 1, not from 0
                anlyDt = StringUtils.getYyyyMmDd(),
                anlySeq = currentAnlySeq
              ),
              pid = pid,
              collDt = value.rsltDt,
              collTm = value.rsltTm,
              anlyTm = StringUtils.getHhMmSs(),
              prdtDt = LocalDate.parse(value.id.msreDt, dateFormat).plusDays(plusIndex.toLong()).format(dateFormat),
              svrtProbMean = "%.3f".format(response.outputs[0].data[requestRowIndex + plusIndex]),
              svrtProbStd = "%.3f".format(response.outputs[1].data[requestRowIndex + plusIndex])
            )
            svrtService.saveSvrtAnly(svrtAnlyRow)
          }
        }
      }
      return response
    } else {
      return null
    }
  }

  fun analyseV4(pid: String) {
    val mntrInfo = MntrInfo()
    val svrtCollList = svrtCollRepository.findByPid(pid)
    log.debug(svrtCollList.size)
    svrtCollList.forEach { svrtColl ->
      if (svrtColl.isMntrInfoValueBlank()) {
        return@forEach
      }
      val date = formatDateString(svrtColl.id.msreDt)
      mntrInfo.inputData(date, svrtColl)
    }

    val objectMapper = ObjectMapper()
    val json = objectMapper.writeValueAsString(mntrInfo)
    val quote = JSONObject.quote(json.toString())
    val requestBody = NubisonSvrtAnlyRequest(inputs = listOf(NubisonSvrtAnlyInputData(data = listOf(json))))
    val res = nubisonAiSeverityAnalysisRestClient.inferV4(
      objectMapper.writeValueAsString(requestBody)
    )
    log.debug(res)
  }


  private fun formatDateString(date: String): String {
    val year = date.substring(0, 4)
    val month = date.substring(4, 6).toInt()
    val day = date.substring(6, 8).toInt()
    return "$year.$month.$day"
  }
}
