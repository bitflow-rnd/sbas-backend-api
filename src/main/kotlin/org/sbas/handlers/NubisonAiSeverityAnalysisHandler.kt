package org.sbas.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.entities.svrt.SvrtColl
import org.sbas.restclients.NubisonAiSeverityAnalysisRestClient
import org.sbas.restdtos.MntrInfo
import org.sbas.restdtos.NubisonSvrtAnlyInputData
import org.sbas.restdtos.NubisonSvrtAnlyRequest

@ApplicationScoped
class NubisonAiSeverityAnalysisHandler {

  @Inject
  private lateinit var log: Logger

  @Inject
  private lateinit var objectMapper: ObjectMapper
  
  @RestClient
  private lateinit var nubisonAiSeverityAnalysisRestClient: NubisonAiSeverityAnalysisRestClient

  fun analyse(pid: String, svrtCollList: List<SvrtColl>): List<Float> {
    val mntrInfo = MntrInfo()
    svrtCollList.forEach { svrtColl ->
      val date = formatDateString(svrtColl.id.msreDt)
      mntrInfo.inputData(date, svrtColl)
    }

    val json = objectMapper.writeValueAsString(mntrInfo)
    val requestBody = NubisonSvrtAnlyRequest(inputs = listOf(NubisonSvrtAnlyInputData(data = listOf(json))))
    val res = nubisonAiSeverityAnalysisRestClient.inferV5(
      objectMapper.writeValueAsString(requestBody)
    )
    return res.outputs[0].data
  }

  private fun formatDateString(date: String): String {
    val year = date.substring(0, 4)
    val month = date.substring(4, 6).toInt()
    val day = date.substring(6, 8).toInt()
    return "$year.$month.$day"
  }
}
