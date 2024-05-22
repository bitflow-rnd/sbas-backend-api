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
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject


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
             * model_name: 모델명
             * model_version: 모델 버전
             * id: 요청 구분 값
             * parameters: None
             * outputs[]: Output 은 모델 추론값 구조. 기존 Outputs은 환자 데이터의 현 시점을 기준으로 현 시점까지의 모든 모델의 평균과 표준편차 그리고 이후 예측에대한 평균과 표준편차를 표시, 변경된 Output 은 CovSF
             * name: CovSF 표시
             * shape: 추론 값의 길이.
             * datatype: 추론 값의 데이터 형. FP64
             * parameters: None 무시
             * data[]: CovSF 값
             */
            val response = nubisonAiSeverityAnalysisRestClient.infer(requestBody)
            val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
            for ((requestRowIndex, value) in svrtCollList.withIndex()) {
                    val svrtAnlyRow = SvrtAnly(
                        SvrtAnlyId(
                            ptId = value.id!!.ptId,
                            hospId = value.id!!.hospId,
                            rgstSeq = value.id!!.rgstSeq,
                            msreDt = LocalDate.parse(value.id!!.msreDt, DateTimeFormatter.ofPattern("yyyy.M.d"))
                                .format(dateFormat),
                            collSeq = requestRowIndex + 1, // +1 is to start from 1, not from 0
                            anlyDt = StringUtils.getYyyyMmDd(),
                            anlySeq = currentAnlySeq
                        ),
                        pid = pid,
                        collDt = value.rsltDt,
                        collTm = value.rsltTm,
                        anlyTm = StringUtils.getHhMmSs(),
                        prdtDt = LocalDate.parse(value.id!!.msreDt, DateTimeFormatter.ofPattern("yyyy.M.d"))
                            .format(dateFormat),
                        CovSF = "%.3f".format(response.outputs[0].data[requestRowIndex])
                    )
                    svrtService.saveSvrtAnly(svrtAnlyRow)
            }
            return response
        } else {
            return null
        }
    }

}