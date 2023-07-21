package org.sbas.handlers

import org.eclipse.microprofile.rest.client.inject.RestClient
import org.sbas.restclients.NubisonAiSeverityAnalysisRestClient
import org.sbas.services.SvrtService
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class NubisonAiSeverityAnalysisHandler {

    @RestClient
    private lateinit var nubisonAiSeverityAnalysisRestClient: NubisonAiSeverityAnalysisRestClient

    @Inject
    private lateinit var svrtService: SvrtService

    fun analyse(pid: String): String {

        val requestData = svrtService.getSvrtRequestData(pid)
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

        return nubisonAiSeverityAnalysisRestClient.infer(requestBody).toString()
    }

}