package org.sbas.handlers

import org.eclipse.microprofile.rest.client.inject.RestClient
import org.sbas.entities.svrt.SvrtAnly
import org.sbas.entities.svrt.SvrtAnlyId
import org.sbas.restclients.NubisonAiSeverityAnalysisRestClient
import org.sbas.restresponses.NubisonAiSeverityAnalysisResponse
import org.sbas.services.SvrtService
import org.sbas.utils.StringUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class NubisonAiSeverityAnalysisHandler {

    @RestClient
    private lateinit var nubisonAiSeverityAnalysisRestClient: NubisonAiSeverityAnalysisRestClient

    @Inject
    private lateinit var svrtService: SvrtService

    @OptIn(ExperimentalStdlibApi::class)
    fun analyse(pid: String): NubisonAiSeverityAnalysisResponse? {

        val svrtCollList = svrtService.getSvrtCollByPidAndMsreDt(pid)?.sortedBy { it.id?.msreDt }
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
            val OUTPUTS_ROW_COUNT = 4
            val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
            for ((requestRowIndex, value) in svrtCollList.withIndex()) {
                for (outputsDataIndex in 0..<OUTPUTS_ROW_COUNT) {
                    val svrtAnlyRow = SvrtAnly(
                        SvrtAnlyId(
                            ptId = value.id!!.ptId,
                            hospId = value.id!!.hospId,
                            rgstSeq = value.id!!.rgstSeq,
                            msreDt = value.id!!.msreDt,
                            collSeq = outputsDataIndex + (OUTPUTS_ROW_COUNT * requestRowIndex) + 1, // +1 is to start from 1, not from 0
                            anlyDt = StringUtils.getYyyyMmDd(),
                            anlySeq = currentAnlySeq
                        ),
                        pid = pid,
                        collDt = value.rsltDt,
                        collTm = value.rsltTm,
                        anlyTm = StringUtils.getHhMmSs(),
                        prdtDt = LocalDate.parse(value.id!!.msreDt, dateFormat).plusDays(outputsDataIndex.toLong())
                            .format(dateFormat),
                        svrtProb = "%.3f".format(response.outputs[outputsDataIndex].data[requestRowIndex]),
                    )
                    svrtService.saveSvrtAnly(svrtAnlyRow)
                }
            }
            return response
        } else {
            return null
        }
    }

}